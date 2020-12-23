package me.danielllewellyn.es

import me.danielllewellyn.es.interfaces.ESEventListener
import me.danielllewellyn.es.interfaces.ESReducer
import me.danielllewellyn.es.interfaces.ESStateListener
import me.danielllewellyn.es.internal.util.ChainedEsReducer

class ESChainReducerBuilder<State, Event>(private val defaultState: State) {

    private val list = mutableListOf<ESReducer<State, Event>>()
    private val stateList = mutableListOf<ESStateListener<State>>()
    private val eventList = mutableListOf<ESEventListener<Event>>()

    fun chain(reducer: ESReducer<State, Event>) {
        list.add(reducer)
    }

    fun chain(state: ESStateListener<State>) {
        stateList.add(state)
    }

    fun chain(event: ESEventListener<Event>) {
        eventList.add(event)
    }

    fun build(): ESEventQueue<State, Event> {
        return DefaultEventQueue(ChainedEsReducer(list), defaultState)
    }

}

fun <State, Event> queueBuilder(defaultState: State, block: ESChainReducerBuilder<State, Event>.() -> Unit) =
    with(ESChainReducerBuilder<State, Event>(defaultState)) {
        block()
        build()
    }