package me.danielllewellyn.es.interfaces

import me.danielllewellyn.es.model.EventModel

interface ESReducer<State, Event> {
    fun State.reduce(event : EventModel<Event>) : State
}

internal class EmptyESReducer<State, Event> : ESReducer<State, Event> {
    override fun State.reduce(event: EventModel<Event>): State {
        return this
    }
}