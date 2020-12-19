package me.danielllewellyn.es.chains

import me.danielllewellyn.es.ESReducer
import me.danielllewellyn.es.model.EventModel

class EventLoggerChain<State, Event>(private val logger: (String) -> Unit) : ESReducer<State, Event> {

    override fun State.reduce(event: EventModel<Event>): State {
        logger("Received event uuid: ${event.uuid} event ${event.value}")
        return this
    }
}