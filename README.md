[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=dllewellyn_event-sourcing-kmp&metric=alert_status)](https://sonarcloud.io/dashboard?id=dllewellyn_event-sourcing-kmp)

# What is this for?


This library is intended to provide functionality cross platform to be able to 
communicate miminal amounts of information across multiple applications. The idea
centres on two concepts


* Events
* State

Let's talk through an example using a note. A sample event might be 'Create note'.

App -> Send event (Create note) -> State is calculated from this state

Now, we might set the title on this state:

App -> Send event (Set note title) -> State is recaculated for this state.


This brings us onto the key components you need to implement in your application:

* Event classes

These events are dataclasses that are able to be serialized. 

* State
  
This a class to encapsulate the current state of an object

* State builders

This is a class which takes all events and calculates the state


Something else to note:

In our example, our "Create note" and "Update note" object pertains to a particular
note. We need a concept them for linking specific events together. Your handler will 
take an item of type `Event` which has a `uid` property on it. If the UID is null it 
is assumed to be a 'global event'. 

Sample usage:

```kotlin
// State: 
data class NoteState(val name : String, val body : String)

// Events:
sealed class NoteEvents {
    object CreateEvent : NoteEvents()
    class UpdateTitle(val newTitle : String) : NoteEvent()
}

// Handler implementation

class NoteEventHandler : ESReducer<NoteState, NoteEvents>() {
    
    override fun NoteState.reduce(event : EventModel<NoteEvents>) =
        when (event) {
           CreateEvent ->  {
                return NoteState("", "")
            }
           is UpdateTitle -> {
               copy(title = event.value.newTitle)
            } 
        }
}
```

# Interfaces

ESReducer - this is used to convert an event into a state
ESEventListener - this is used to listen for events
ESState - after all reducers have run, the new state is emitted

# Queue 

You can add an item to a queue when using the queue builder 

