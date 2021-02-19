package me.danielllewellyn.es

import kotlinx.coroutines.CoroutineScope
import me.danielllewellyn.es.interfaces.ESEventListener
import me.danielllewellyn.es.interfaces.ESReducer
import me.danielllewellyn.es.interfaces.ESStateListener
import me.danielllewellyn.es.internal.util.*
import me.danielllewellyn.es.internal.util.ChainedConditionalESEventListener
import me.danielllewellyn.es.internal.util.ChainedConditionalEsReducer
import me.danielllewellyn.es.internal.util.ChainedEsReducer

typealias Conditional<T> = (T) -> Boolean

class ESChainReducerBuilder<State, Event>(private val scope : CoroutineScope, private val defaultState: State) {

    private val list = mutableListOf<ESReducer<State, Event>>()
    private val stateList = mutableListOf<ESStateListener<State>>()
    private val eventList = mutableListOf<ESEventListener<Event>>()

    private val conditionalList = mutableListOf<Pair<ESReducer<State, Event>, Conditional<Event>>>()
    private val conditionalEventList = mutableListOf<Pair<ESEventListener<Event>, Conditional<Event>>>()


    fun chain(reducer: ESReducer<State, Event>) {
        list.add(reducer)
    }

    fun chain(state: ESStateListener<State>) {
        stateList.add(state)
    }

    fun chain(event: ESEventListener<Event>) {
        eventList.add(event)
    }

    fun conditionalChain(event: ESEventListener<Event>, condition: Conditional<Event>) {
        conditionalEventList.add(Pair(event, condition))
    }

    fun conditionalChain(event: ESReducer<State, Event>, condition: Conditional<Event>) {
        conditionalList.add(Pair(event, condition))
    }

    fun build(): ESEventQueue<State, Event> {
        eventList.add(ChainedConditionalESEventListener(conditionalEventList))
        list.add(ChainedConditionalEsReducer(conditionalList))

        return DefaultEventQueue(
            ChainedEsReducer(list),
            ChainEsEventListener(eventList),
            ChainedEsStateListener(stateList),
            scope,
            defaultState
        )
    }

}

fun <State, Event> queueBuilder(scope : CoroutineScope, defaultState: State, block: ESChainReducerBuilder<State, Event>.() -> Unit) =
    with(ESChainReducerBuilder<State, Event>(scope, defaultState)) {
        block()
        build()
    }