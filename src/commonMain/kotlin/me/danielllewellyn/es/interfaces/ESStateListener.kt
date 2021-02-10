package me.danielllewellyn.es.interfaces

interface ESStateListener<State> {
    suspend fun onState(state: State)
}


internal class EmptyESStateListener<State> : ESStateListener<State> {
    override suspend fun onState(state: State) {
        // NOOP
    }

}