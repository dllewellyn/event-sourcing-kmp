package me.danielllewellyn.es.model

sealed class EventStorage<T>(val type: String) {
    data class EventStorageItem<T>(val eventType: String, val uuid: String, val item: T) : EventStorage<T>(eventType) {
        fun toRemoveEvent() = RemoveEvent<T>(eventType, uuid)
    }

    data class RemoveEvent<T>(val eventType: String, val uuid: String) :
        EventStorage<T>(eventType)
}