package me.danielllewellyn.es

import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import me.daniellewellyn.es.Events
import me.danielllewellyn.es.chains.EventLoggerChain
import me.danielllewellyn.es.chains.StateLoggerChain
import me.danielllewellyn.es.chains.StorageChain
import me.danielllewellyn.es.ESDriverFactory
import me.danielllewellyn.es.model.EventModel
import org.junit.Test

// State:
data class NoteState(val name: String, val body: String)

// Events:
@Serializable
sealed class NoteEvent {
    @Serializable
    object CreateEvent : NoteEvent()

    @Serializable
    data class UpdateTitle(val newTitle: String) : NoteEvent()
}

// Handler implementation

class NoteEventHandler : ESReducer<NoteState, NoteEvent> {

    override fun NoteState.reduce(event: EventModel<NoteEvent>) =
        when (val v = event.value) {
            NoteEvent.CreateEvent -> {
                NoteState("", "")
            }
            is NoteEvent.UpdateTitle -> {
                copy(name = v.newTitle)
            }
        }
}

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class MainTest {

    @Test
    fun `test that we can setup a sample new title`() {

        val queue = queueBuilder<NoteState, NoteEvent>(NoteState("", "")) {
            chain(EventLoggerChain(::println))
            chain(NoteEventHandler())
        }

        GlobalScope.launch {
            queue.run()

            val event = EventModel.new<NoteEvent>(NoteEvent.CreateEvent)
            queue.processEvent(event)
            queue.processEvent(EventModel(event.uuid, NoteEvent.UpdateTitle("NewTitle")))

        }

//        runBlocking {
//            with(queue.states().take(2).toList()) {
//                assertThat(first().name).isEmpty()
//                assertThat(last().name).isEqualTo("NewTitle")
//            }
//        }
    }

    @Test
    fun `test that we can use the store chain`() {

        val events = Events(ESDriverFactory().createDriver())

        val queue = queueBuilder<NoteState, NoteEvent>(NoteState("", "")) {
            chain(EventLoggerChain(::println))
            chain(StorageChain(events, NoteEvent.serializer()))
            chain(NoteEventHandler())
            chain(StateLoggerChain(::println))
        }

        GlobalScope.launch {
            queue.run()

            val event = EventModel.new<NoteEvent>(NoteEvent.CreateEvent)
            queue.processEvent(event)
            queue.processEvent(EventModel(event.uuid, NoteEvent.UpdateTitle("NewTitle")))

        }

        runBlocking {
//            with(queue.states().take(2).toList()) {
//                assertThat(first().name).isEmpty()
//                assertThat(last().name).isEqualTo("NewTitle")
//            }

//            async {
//                ESEventReplayer(events)
//                    .events(NoteEvent.serializer())
//                    .collect { println(it) }
//            }
//
//            delay(10)

        }
    }

}