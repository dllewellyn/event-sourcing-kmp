package me.danielllewellyn.es.chains

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import me.daniellewellyn.es.Events
import me.danielllewellyn.es.ESEventQueue
import me.danielllewellyn.es.interfaces.ESEventListener
import me.danielllewellyn.es.internal.util.uuid
import me.danielllewellyn.es.model.EventModel
import me.danielllewellyn.es.model.StorageActionEvent
import me.danielllewellyn.es.model.StorageEvent
import me.danielllewellyn.es.queueBuilder
import me.danielllewellyn.es.queues.StorageActionQueue
import me.dllewellyn.es.EventsTable

class StorageChain<EventGeneric>(
    private val events: Events,
    private val eventValueSerializer: SerializationStrategy<EventGeneric>,
    private val eventValueDeserializer: DeserializationStrategy<EventGeneric>,
    private val onwardQueue: ESEventQueue<Unit, StorageEvent<EventGeneric>>,
    private val queueName: String,
    private val scope: CoroutineScope = GlobalScope
) : ESEventListener<EventGeneric>, StorageActionQueue() {

    init {
        refresh(scope)
    }

    override suspend fun processEvent(event: EventModel<StorageActionEvent>) {
        when (event.value) {
            StorageActionEvent.RefreshStorage -> refresh(scope = scope)
        }
    }
    private fun refresh(scope: CoroutineScope) {
        scope.launch {
            events.eventDatabaseQueries.allEventsForName(queueName).executeAsList().asFlow()
                .collect { item ->
                    onwardQueue.processEvent(
                        EventModel.new(
                            StorageEvent(
                                item.event_uuid,
                                item.item_uuid,
                                Json.decodeFromString(eventValueDeserializer, item.event),
                            ) {
                                events.eventDatabaseQueries.deleteEvent(item.event_uuid)
                            }
                        )
                    )
                }
        }
    }

    override suspend fun onEvent(event: EventModel<EventGeneric>) {
        with(
            EventsTable(
                event_uuid = uuid(),
                item_uuid = event.uuid,
                event = Json.encodeToString(eventValueSerializer, event.value),
                eventName = queueName
            )
        ) {
            events.eventDatabaseQueries.insertEvent(this)

            val onwardEvent = EventModel.new(StorageEvent(event_uuid, item_uuid, event.value) {
                events.eventDatabaseQueries.deleteEvent(event.uuid)
            })
            onwardQueue.processEvent(onwardEvent)
        }


    }

}