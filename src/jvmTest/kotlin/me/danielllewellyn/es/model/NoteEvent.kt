package me.danielllewellyn.es.model

import kotlinx.serialization.Serializable

// Events:
@Serializable
sealed class NoteEvent {
    @Serializable
    object CreateEvent : NoteEvent()

    @Serializable
    data class UpdateTitle(val newTitle: String) : NoteEvent()

    companion object {
        const val NoteEventName = "note_event"
    }
}