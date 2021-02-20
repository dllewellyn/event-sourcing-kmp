package me.danielllewellyn.es.interfaces

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import me.danielllewellyn.es.model.EventStorage

interface ESEventStorage {
    suspend fun <T> storeEvent(
        type: String,
        serialization: SerializationStrategy<T>,
        event: T,
        uuid: String
    )

    suspend fun <T> retrieveAllStoredEventsForType(
        type: String,
        deserializationStrategy: DeserializationStrategy<T>
    ): List<EventStorage.EventStorageItem<T>>

    suspend fun <T>removeItem(removeEventStorage: EventStorage.RemoveEvent<T>)
}


