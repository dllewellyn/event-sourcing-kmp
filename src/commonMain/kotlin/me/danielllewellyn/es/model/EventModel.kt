package me.danielllewellyn.es.model

import me.danielllewellyn.es.internal.util.uuid

data class EventModel<T>(val uuid : String, val value : T) {

    companion object {
        fun <T>new(value: T) =
            EventModel(uuid(), value)
    }
}