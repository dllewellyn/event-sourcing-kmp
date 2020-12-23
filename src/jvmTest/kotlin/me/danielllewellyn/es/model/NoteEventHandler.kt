package me.danielllewellyn.es.model

import me.danielllewellyn.es.interfaces.ESReducer

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