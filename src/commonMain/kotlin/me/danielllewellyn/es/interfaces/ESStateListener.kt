package me.danielllewellyn.es.interfaces

interface ESStateListener<State> {
    suspend fun onState(state: State)
}