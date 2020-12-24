package me.danielllewellyn.es

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import me.danielllewellyn.es.interfaces.ESEventListener
import me.danielllewellyn.es.interfaces.ESReducer
import me.danielllewellyn.es.interfaces.ESStateListener
import me.danielllewellyn.es.model.EventModel

interface ESEventQueue<State, Event> {
    suspend fun processEvent(event: EventModel<Event>)
    fun startQueue(context: CoroutineScope = GlobalScope)
    fun states(): Flow<State>
    fun events(): Flow<EventModel<Event>>
    fun cachedValue(): State
}

class DefaultEventQueue<State, Event>(
    private val reducer: ESReducer<State, Event>,
    private val eventLogger: ESEventListener<Event>,
    private val stateListener: ESStateListener<State>,
    defaultState: State
) :
    ESEventQueue<State, Event> {

    private val memoryEventQueue = MutableSharedFlow<EventModel<Event>>()

    private val internalStateHolder = MutableSharedFlow<State>()
    private var cached: State = defaultState

    override suspend fun processEvent(event: EventModel<Event>) {
        eventLogger.onEvent(event)
        memoryEventQueue.emit(event)
    }

    override fun startQueue(context: CoroutineScope) {
        context.async {
            memoryEventQueue.collect { event ->
                internalStateHolder.emit(
                    reducer.run {
                        cached.reduce(event)
                    }.also {
                        stateListener.onState(it)
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

    override fun events(): Flow<EventModel<Event>> {
        return memoryEventQueue
    }

}