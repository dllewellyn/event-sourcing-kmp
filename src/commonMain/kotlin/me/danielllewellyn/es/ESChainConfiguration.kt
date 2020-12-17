package me.danielllewellyn.es

import me.danielllewellyn.es.internal.util.ChainedEsReducer

class ESChainReducerBuilder<State, Event>(private val defaultState: State) {

    private val list = mutableListOf<ESReducer<State, Event>>()

    fun add(reducer: ESReducer<State, Event>) {
        list.add(reducer)
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


fun <State, Event> ESChainReducerBuilder<State, Event>.chain(config: ESReducer<State, Event>): ESChainReducerBuilder<State, Event> {
    add(reducer = config)
    return this
}
