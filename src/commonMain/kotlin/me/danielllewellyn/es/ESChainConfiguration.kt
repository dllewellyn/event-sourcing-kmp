package me.danielllewellyn.es

import me.danielllewellyn.es.interfaces.ESEventListener
import me.danielllewellyn.es.internal.util.ChainEsEventListener
import me.danielllewellyn.es.internal.util.ChainedConditionalESEventListener

typealias Conditional<T> = (T) -> Boolean

class ESChainReducerBuilder<Event> {

    private val eventList = mutableListOf<ESEventListener<Event>>()

    private val conditionalEventList = mutableListOf<Pair<ESEventListener<Event>, Conditional<Event>>>()


    fun chain(event: ESEventListener<Event>) {
        eventList.add(event)
    }

    fun conditionalChain(event: ESEventListener<Event>, condition: Conditional<Event>) {
        conditionalEventList.add(Pair(event, condition))
    }

    fun build(): ESEventQueue<Event> {
        eventList.add(ChainedConditionalESEventListener(conditionalEventList))
        return DefaultEventQueue(ChainEsEventListener(eventList))
    }

}

fun <Event> queueBuilder(
    block: ESChainReducerBuilder<Event>.() -> Unit
) =
    with(ESChainReducerBuilder< Event>()) {
        block()
        build()
    }