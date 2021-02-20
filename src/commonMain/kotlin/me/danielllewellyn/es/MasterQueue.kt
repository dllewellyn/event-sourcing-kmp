package me.danielllewellyn.es

object MasterQueue {

    private val handlers = mutableMapOf<String, ESEventQueue<*>>()

    fun <State, Event> register(name: String, esReducer: ESEventQueue<Event>) {
        handlers[name] = esReducer
    }

    fun <State, Event> queueForEvent(name: String) = handlers[name] as ESEventQueue<Event>

}