package me.danielllewellyn.es.internal.util

import me.danielllewellyn.es.Conditional
import me.danielllewellyn.es.interfaces.ESEventListener
import me.danielllewellyn.es.model.EventModel

internal class ChainedConditionalESEventListener<Event>(private val listeners: List<Pair<ESEventListener<Event>, Conditional<Event>>>) :
    ESEventListener<Event> {

    override suspend fun onEvent(event: EventModel<Event>) {
        listeners.forEach {
            if (it.second(event.value)) {
                it.first.onEvent(event)
            }
        }
    }
}