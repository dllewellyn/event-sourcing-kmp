package me.danielllewellyn.es.model

sealed class StorageActionEvent {
    object RefreshStorage : StorageActionEvent()
}