package me.danielllewellyn.es.chains

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer
import me.danielllewellyn.es.ESConfiguration
import me.danielllewellyn.es.ESEventQueue
import me.danielllewellyn.es.interfaces.ESEventListener
import me.danielllewellyn.es.interfaces.ESEventStorage
import me.danielllewellyn.es.model.EventModel
import me.danielllewellyn.es.model.EventStorage
import me.danielllewellyn.es.queues.StorageActionQueue

class StorageChain<EventGeneric>(
    private val eventStorage: ESEventStorage,
    private val eventValueSerializer: KSerializer<EventGeneric>,
    private val onwardQueue: ESEventQueue<EventGeneric>,
    replayOnLaunch: Boolean,
    private val deleteAfterCompletion: Boolean,
    eventType: String,
    scope: CoroutineScope
) : StorageActionQueue<EventGeneric>(eventType), ESEventListener<EventGeneric> {

    init {
        if (replayOnLaunch) {
            refresh(scope)
        }
    }

    override suspend fun processEvent(event: EventModel<EventStorage<EventGeneric>>) {
        when (val v = event.value) {
            is EventStorage.EventStorageItem -> eventStorage.storeEvent(
                v.eventType,
                eventValueSerializer,
                v.item,
                v.uuid
            ).also {
                onwardQueue.processEvent(EventModel(event.uuid, v.item))
                if (deleteAfterCompletion) {
                    processEvent(EventModel.new(v.toRemoveEvent()))
                }
            }
            is EventStorage.RemoveEvent -> eventStorage.removeItem(v)
        }
    }

    private fun refresh(scope: CoroutineScope) {
        ESConfiguration.verboseLogger?.invoke("Going to load events")
        scope.launch {
            eventStorage.retrieveAllStoredEventsForType(eventType, eventValueSerializer)
                .also { ESConfiguration.verboseLogger?.invoke("Loaded ${it.size} events from storage for ${eventType}}") }
                .asFlow()
                .collect {
                    onwardQueue.processEvent(EventModel(it.uuid, it.item))
                    if (deleteAfterCompletion) {
                        processEvent(EventModel.new(it.toRemoveEvent()))
                    }
                }
        }
    }

    override suspend fun onEvent(event: EventModel<EventGeneric>) {
        processEvent(EventModel(event.uuid, EventStorage.EventStorageItem(eventType, event.uuid, event.value)))
    }
}