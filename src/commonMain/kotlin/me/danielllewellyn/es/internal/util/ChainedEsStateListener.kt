package me.danielllewellyn.es.internal.util

import me.danielllewellyn.es.interfaces.ESStateListener

class ChainedEsStateListener<State>(private val states: List<ESStateListener<State>>) : ESStateListener<State> {
    override suspend fun onState(state: State) {
        states.forEach {
            it.onState(state)
        }
    }
}