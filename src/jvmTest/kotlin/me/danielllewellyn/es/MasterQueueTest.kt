package me.danielllewellyn.es

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import me.danielllewellyn.es.chains.EventLoggerChain
import me.danielllewellyn.es.chains.StorageChain
import me.danielllewellyn.es.interfaces.ESEventStorage
import me.danielllewellyn.es.model.EventModel
import me.danielllewellyn.es.model.EventStorage
import org.junit.Test
import kotlin.test.assertEquals

@Serializable
sealed class DemoEvents {
    @Serializable
    data class Create(val text: String, val uuid: String) : DemoEvents()

    @Serializable
    data class Update(val text: String, val uuid: String) : DemoEvents()

    @Serializable
    data class Remove(val uuid: String) : DemoEvents()
}

data class Note(val text: String, val uuid: String)

class MemoryStorageChain : ESEventStorage {
    private val inMemory: MutableMap<String, Map<String, String>> = mutableMapOf()

    override suspend fun <T> storeEvent(type: String, serialization: SerializationStrategy<T>, event: T, uuid: String) {
        if (inMemory.containsKey(type)) {
            inMemory[type]!!.toMutableMap()[uuid] = Json.encodeToString(serialization, event)
        } else {
            inMemory[type] = mutableMapOf(uuid to Json.encodeToString(serialization, event))
        }
    }

    override suspend fun <T> retrieveAllStoredEventsForType(
        type: String,
        deserializationStrategy: DeserializationStrategy<T>
    ): List<EventStorage.EventStorageItem<T>> {
        return inMemory[type]?.map {
            EventStorage.EventStorageItem(type, it.key, Json.decodeFromString(deserializationStrategy, it.value))
        } ?: emptyList()
    }

    override suspend fun <T> removeItem(removeEventStorage: EventStorage.RemoveEvent<T>) {
        inMemory[removeEventStorage.eventType]?.toMutableMap()?.remove(removeEventStorage.uuid)
    }
}

internal class MasterQueueTest {

    class FakeRepository : ESEventQueue<DemoEvents> {

        private val _repository = mutableListOf<Note>()
        val repository: List<Note> = _repository

        override suspend fun processEvent(event: EventModel<DemoEvents>) {
            println(event)
            when (val v = event.value) {
                is DemoEvents.Create -> _repository.add(Note(v.text, v.uuid))
                is DemoEvents.Update -> with(_repository.firstOrNull { it.uuid == v.uuid }) {
                    this?.let {
                        _repository.remove(it)
                        _repository.add(it.copy(text = v.text))
                    }
                }
                is DemoEvents.Remove -> _repository.removeAll { it.uuid == v.uuid }
            }.let {}
        }

    }

    @Test
    fun `test that we can push something to the master queue`() {

        ESConfiguration.infoLogger = ::println
        ESConfiguration.verboseLogger = ::println

        val repository = FakeRepository()

        runBlocking {

            val storageQueue = queueBuilder<DemoEvents> {
                chain(
                    StorageChain(
                        MemoryStorageChain(),
                        DemoEvents.serializer(),
                        repository,
                        true,
                        "demo_events",
                        GlobalScope
                    )
                )
            }

            storageQueue.processEvent(EventModel.new(DemoEvents.Create("Abc", "123")))
            storageQueue.processEvent(EventModel.new(DemoEvents.Create("Def", "234")))
            storageQueue.processEvent(EventModel.new(DemoEvents.Update("Hij", "123")))
            storageQueue.processEvent(EventModel.new(DemoEvents.Remove("234")))

            with(assertThat(repository.repository)) {
                contains(Note("Hij", "123"))
                hasSize(1)
            }

        }


    }
}