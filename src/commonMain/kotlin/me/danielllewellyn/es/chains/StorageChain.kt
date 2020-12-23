package me.danielllewellyn.es.chains

import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import me.daniellewellyn.es.Events
import me.danielllewellyn.es.interfaces.ESEventListener
import me.danielllewellyn.es.interfaces.ESReducer
import me.danielllewellyn.es.internal.util.uuid
import me.danielllewellyn.es.model.EventModel
import me.dllewellyn.es.EventsTable

class StorageChain<EventGeneric>(
    private val events: Events,
    private val eventValueSerializer: SerializationStrategy<EventGeneric>
) : ESEventListener<EventGeneric> {

    override suspend fun onEvent(event: EventModel<EventGeneric>) {
        events.eventDatabaseQueries.insertEvent(
            EventsTable(
                event_uuid = uuid(),
                item_uuid = event.uuid,
                event = Json.encodeToString(eventValueSerializer, event.value)
            )
        )
    }

}