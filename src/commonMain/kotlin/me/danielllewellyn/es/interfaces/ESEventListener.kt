package me.danielllewellyn.es.interfaces

import me.danielllewellyn.es.model.EventModel

interface ESEventListener<Event> {

    suspend fun onEvent(event : EventModel<Event>)
}


internal class EmptyListener<Event> : ESEventListener<Event> {
    override suspend fun onEvent(event: EventModel<Event>) {
        // NOOP
    }
}