package me.danielllewellyn.es

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import me.danielllewellyn.es.MasterQueue.queueForEvent
import me.danielllewellyn.es.MasterQueue.register
import me.danielllewellyn.es.model.EventModel
import me.danielllewellyn.es.model.NoteEvent
import me.danielllewellyn.es.model.NoteEvent.Companion.NoteEventName
import me.danielllewellyn.es.model.NoteEventHandler
import me.danielllewellyn.es.model.NoteState
import org.junit.Test


internal class MasterQueueTest {

    @Test
    fun `test that we can push something to the master queue`() {
        val queue = queueBuilder<NoteState, NoteEvent>(NoteState("", "")) {
            chain(NoteEventHandler())
        }

        GlobalScope.launch {
            queue.startQueue()

            register(NoteEventName, queue)
            with(queueForEvent<NoteState, NoteEvent>(NoteEventName)) {
                val note: EventModel<NoteEvent> = EventModel.new(NoteEvent.CreateEvent)
                processEvent(note)
                processEvent(EventModel(note.uuid, NoteEvent.UpdateTitle("NewTitle")))
            }
        }

        runBlocking {
            val state: NoteState = queue.states().take(2).toList().last()
            assertThat(state).isEqualTo(NoteState("NewTitle", ""))
        }


    }
}