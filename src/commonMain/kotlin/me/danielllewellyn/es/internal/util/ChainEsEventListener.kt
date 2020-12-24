package me.danielllewellyn.es.internal.util

import me.danielllewellyn.es.interfaces.ESEventListener
import me.danielllewellyn.es.model.EventModel

class ChainEsEventListener<Event>(private val listeners: List<ESEventListener<Event>>) : ESEventListener<Event> {

    override suspend fun onEvent(event: EventModel<Event>) {
        listeners.forEach {
            it.onEvent(event)
        }
    }
}