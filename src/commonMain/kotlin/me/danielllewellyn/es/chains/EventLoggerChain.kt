package me.danielllewellyn.es.chains

import me.danielllewellyn.es.interfaces.ESEventListener
import me.danielllewellyn.es.model.EventModel

class EventLoggerChain<Event>(private val logger: (String) -> Unit) : ESEventListener<Event> {
    override suspend fun onEvent(event: EventModel<Event>) {
        logger("Received event uuid: ${event.uuid} event ${event.value}")
    }

}