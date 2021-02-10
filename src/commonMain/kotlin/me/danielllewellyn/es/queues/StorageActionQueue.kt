package me.danielllewellyn.es.queues

import me.danielllewellyn.es.DefaultEventQueue
import me.danielllewellyn.es.interfaces.EmptyESReducer
import me.danielllewellyn.es.interfaces.EmptyESStateListener
import me.danielllewellyn.es.interfaces.EmptyListener
import me.danielllewellyn.es.model.StorageActionEvent

open class StorageActionQueue : DefaultEventQueue<Unit, StorageActionEvent>(
    defaultState = Unit,
    reducer = EmptyESReducer(),
    stateListener = EmptyESStateListener(),
    eventLogger = EmptyListener()
)