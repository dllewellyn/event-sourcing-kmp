package me.danielllewellyn.es.queues

import me.danielllewellyn.es.DefaultEventQueue
import me.danielllewellyn.es.interfaces.EmptyListener
import me.danielllewellyn.es.model.EventStorage

open class StorageActionQueue<T>(val eventType: String) :
    DefaultEventQueue<EventStorage<T>>(
        eventLogger = EmptyListener()
    )
