package me.danielllewellyn.es.queues

import kotlinx.coroutines.CoroutineScope
import me.danielllewellyn.es.DefaultEventQueue
import me.danielllewellyn.es.interfaces.EmptyESReducer
import me.danielllewellyn.es.interfaces.EmptyESStateListener
import me.danielllewellyn.es.interfaces.EmptyListener
import me.danielllewellyn.es.model.StorageActionEvent

open class StorageActionQueue(scope: CoroutineScope) : DefaultEventQueue<Unit, StorageActionEvent>(
    defaultState = Unit,
    reducer = EmptyESReducer(),
    stateListener = EmptyESStateListener(),
    eventLogger = EmptyListener(),
    scope = scope
)