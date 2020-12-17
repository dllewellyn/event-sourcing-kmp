package me.danielllewellyn.es

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import me.danielllewellyn.es.model.EventModel

interface ESEventQueue<State, Event> {
    suspend fun processEvent(event: EventModel<Event>)
    fun run(context: CoroutineScope = GlobalScope)
    fun states(): Flow<State>
    fun cachedValue() : State
}

class DefaultEventQueue<State, Event>(private val reducer: ESReducer<State, Event>, defaultState: State) :
    ESEventQueue<State, Event> {

    private val memoryEventQueue = MutableSharedFlow<EventModel<Event>>()
    private val internalStateHolder = MutableSharedFlow<State>()
    private var cached: State = defaultState

    override suspend fun processEvent(event: EventModel<Event>) {
        memoryEventQueue.emit(event)
    }

    override fun run(context: CoroutineScope) {
         context.async {
            memoryEventQueue.collect { event ->
                internalStateHolder.emit(
                    reducer.run {
                        cached.reduce(event)
                    }
                )
            }
        }
    }

    override fun states(): Flow<State> {
        return internalStateHolder
    }

    override fun cachedValue(): State {
        return cached
    }

}