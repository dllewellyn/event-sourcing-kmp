package me.danielllewellyn.es

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import me.danielllewellyn.es.chains.LoggerChain
import me.danielllewellyn.es.model.EventModel
import org.junit.Test

// State:
data class NoteState(val name: String, val body: String)

// Events:
sealed class NoteEvent {
    object CreateEvent : NoteEvent()
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
            chain(LoggerChain(::println))
            chain(NoteEventHandler())
        }

        GlobalScope.launch {
            queue.run()

            val event = EventModel.new<NoteEvent>(NoteEvent.CreateEvent)
            queue.processEvent(event)
            queue.processEvent(EventModel(event.uuid, NoteEvent.UpdateTitle("NewTitle")))

        }

        runBlocking {
            with(queue.states().take(2).toList()) {
                assertThat(first().name).isEmpty()
                assertThat(last().name).isEqualTo("NewTitle")
            }
        }
    }

}