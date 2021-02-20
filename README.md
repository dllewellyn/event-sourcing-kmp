# What is this for?


This library is intended to provide functionality cross platform to be able to fire and respond to events,
as well as store and those events for future processing.

* Events

Let's talk through an example using a note. A sample event might be 'Create note'.

App -> Send event (Create note) -> Repository listens to the event and creates a note

Now, we might set the title on this item:

App -> Send event (Set note title) -> Repository gets event and changes the notes title


Sample usage:

```kotlin
// Events 

@Serializable
sealed class DemoEvents {
    @Serializable
    data class Create(val text: String, val uuid: String) : DemoEvents()

    @Serializable
    data class Update(val text: String, val uuid: String) : DemoEvents()

    @Serializable
    data class Remove(val uuid: String) : DemoEvents()
}

// A single note
data class Note(val text: String, val uuid: String)

// Sample repository
class FakeRepository : ESEventQueue<DemoEvents> {

    private val _repository = mutableListOf<Note>()
    val repository: List<Note> = _repository

    override suspend fun processEvent(event: EventModel<DemoEvents>) {
        println(event)
        when (val v = event.value) {
            is DemoEvents.Create -> _repository.add(Note(v.text, v.uuid))
            is DemoEvents.Update -> with(_repository.firstOrNull { it.uuid == v.uuid }) {
                this?.let {
                    _repository.remove(it)
                    _repository.add(it.copy(text = v.text))
                }
            }
            is DemoEvents.Remove -> _repository.removeAll { it.uuid == v.uuid }
        }.let {}
    }

}

val repository = FakeRepository()

runBlocking {

    val storageQueue = queueBuilder<DemoEvents> {
        chain(
            StorageChain(
                MemoryStorageChain(),
                DemoEvents.serializer(),
                repository,
                "demo_events",
                GlobalScope
            )
        )
    }

    storageQueue.processEvent(EventModel.new(DemoEvents.Create("Abc", "123")))
    storageQueue.processEvent(EventModel.new(DemoEvents.Create("Def", "234")))
    storageQueue.processEvent(EventModel.new(DemoEvents.Update("Hij", "123")))
    storageQueue.processEvent(EventModel.new(DemoEvents.Remove("234")))

    with(assertThat(repository.repository)) {
        contains(Note("Hij", "123"))
        hasSize(1)
    }

}

```

# Interfaces

ESEventListener - this is used to listen for events

# Storage

There are three-ish use cases for storage. 

1. You want to permanently store events, but not replay them on launch
2. You want to store events, but remove them when the next stage in the queue is processed. For example,
    you might want to store an event then send it to a remote server. If sending fails, you might want to
    retry the next time the user launches your app
3. You might want to store events, then replay them every time you launch the app. For example, you might
    not store your repository on disk but instead recalculate it every time
   


