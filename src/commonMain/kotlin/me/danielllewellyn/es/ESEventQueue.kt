package me.danielllewellyn.es

import me.danielllewellyn.es.interfaces.ESEventListener
import me.danielllewellyn.es.model.EventModel

interface ESEventQueue<Event> {
    suspend fun processEvent(event: EventModel<Event>)
}

open class DefaultEventQueue<Event>(
    private val eventLogger: ESEventListener<Event>
) : ESEventQueue<Event> {

    override suspend fun processEvent(event: EventModel<Event>) {
        ESConfiguration.verboseLogger?.invoke("Process event ${event.toString()}")
        eventLogger.onEvent(event)
    }

}