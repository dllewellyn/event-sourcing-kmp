package me.danielllewellyn.es

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import me.daniellewellyn.es.Events
import me.danielllewellyn.es.chains.EventLoggerChain
import me.danielllewellyn.es.chains.StateLoggerChain
import me.danielllewellyn.es.chains.StorageChain
import me.danielllewellyn.es.model.*
import org.junit.Test

// Handler implementation

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class MainFunctionalityTest {

    @Test
    fun `test that we can setup a sample new title`() {

        val queue = queueBuilder<NoteState, NoteEvent>(NoteState("", "")) {
            chain(EventLoggerChain(::println))
            chain(NoteEventHandler())
        }

        GlobalScope.launch {
            queue.startQueue()

            val event = EventModel.new<NoteEvent>(NoteEvent.CreateEvent)
            queue.processEvent(event)
            queue.processEvent(EventModel(event.uuid, NoteEvent.UpdateTitle("NewTitle")))

        }

        runBlocking {
//            val queueItem: List<NoteState> = queue.states().take(2).toList()
//            assertThat(queueItem.first().name).isEmpty()
//            assertThat(queueItem.last().name).isEqualTo("NewTitle")
        }
    }

    @Test
    fun `test that we can use the store chain`() {

        val events = Events(ESDriverFactory().createDriver())

        val onwardQueue = queueBuilder<Unit, StorageEvent<NoteEvent>>(Unit) {
            chain(EventLoggerChain(::println))
        }

        val queue = queueBuilder<NoteState, NoteEvent>(NoteState("", "")) {
            chain(StorageChain(events, NoteEvent.serializer(), NoteEvent.serializer(), onwardQueue, "NoteQueue"))
            chain(NoteEventHandler())
            chain(StateLoggerChain(::println))
        }

        GlobalScope.launch {
            queue.startQueue()

            val event = EventModel.new<NoteEvent>(NoteEvent.CreateEvent)
            queue.processEvent(event)
            queue.processEvent(EventModel(event.uuid, NoteEvent.UpdateTitle("NewTitle")))

        }

        runBlocking {
//            val queueItem: List<NoteState> = queue.states().take(2).toList()
//            assertThat(queueItem.first().name).isEmpty()
//            assertThat(queueItem.last().name).isEqualTo("NewTitle")
        }
    }

}