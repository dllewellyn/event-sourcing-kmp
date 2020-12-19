package me.danielllewellyn.es

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.map
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import me.daniellewellyn.es.Events
import me.danielllewellyn.es.model.EventModel

class ESEventReplayer(private val event: Events) {

    fun <Event> events(serializer: KSerializer<Event>, itemUid: String? = null) =
        with(itemUid?.let {
            event.eventDatabaseQueries.eventsForItemUuid(itemUid)
        } ?: event.eventDatabaseQueries.allEvents()) {
            asFlow()
                .mapToList()
                .map {
                    it.map { item ->
                        EventModel(item.item_uuid, Json.decodeFromString(serializer, item.event))
                    }
                }
        }
}

