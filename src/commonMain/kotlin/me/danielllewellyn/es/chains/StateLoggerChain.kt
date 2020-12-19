package me.danielllewellyn.es.chains

import me.danielllewellyn.es.ESReducer
import me.danielllewellyn.es.model.EventModel

class StateLoggerChain<State, Event>(private val logger: (String) -> Unit) : ESReducer<State, Event> {

    override fun State.reduce(event: EventModel<Event>): State {
        logger("Got state $this")
        return this
    }
}