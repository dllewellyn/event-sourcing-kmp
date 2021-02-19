package me.danielllewellyn.es.model

sealed class StorageActionEvent {
    object RefreshStorage : StorageActionEvent()
    data class DeleteStorageItem(val uuid : String) : StorageActionEvent()
}