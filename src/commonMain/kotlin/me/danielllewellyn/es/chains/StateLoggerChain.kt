package me.danielllewellyn.es.chains

import me.danielllewellyn.es.interfaces.ESStateListener

class StateLoggerChain<State>(private val logger: (String) -> Unit) : ESStateListener<State> {
    override suspend fun onState(state: State) {
        logger("Got state $state")
    }

}