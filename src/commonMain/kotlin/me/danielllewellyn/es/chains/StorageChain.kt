package me.danielllewellyn.es.chains

import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import me.daniellewellyn.es.Events
import me.danielllewellyn.es.ESReducer
import me.danielllewellyn.es.internal.util.uuid
import me.danielllewellyn.es.model.EventModel
import me.dllewellyn.es.EventsTable

class StorageChain<State, EventGeneric>(
    private val events: Events,
    private val eventValueSerializer: SerializationStrategy<EventGeneric>
) : ESReducer<State, EventGeneric> {

    override fun State.reduce(event: EventModel<EventGeneric>): State {

        events.eventDatabaseQueries.insertEvent(
            EventsTable(
                event_uuid = uuid(),
                item_uuid = event.uuid,
                event = Json.encodeToString(eventValueSerializer, event.value)
            )
        )

        return this
    }
}