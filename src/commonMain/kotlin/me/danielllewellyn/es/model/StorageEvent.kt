package me.danielllewellyn.es.model


data class StorageEvent<T>(val uuid: String, val itemUuid: String, val event: T, val confirmDone: () -> Unit)