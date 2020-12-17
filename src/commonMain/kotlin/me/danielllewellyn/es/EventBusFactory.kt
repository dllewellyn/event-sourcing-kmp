@file:Suppress("UNCHECKED_CAST")

package me.danielllewellyn.es

class EventBusFactory(private val reducers: Map<String, ESReducer<Any, Any>>) {

    fun <T, E> queueForTag(tag: String, default: T) =
        DefaultEventQueue(reducer = reducers[tag] as ESReducer<T, E>, default).also {
            it.run()
        }
}


