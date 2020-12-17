package me.danielllewellyn.es

import me.danielllewellyn.es.model.EventModel

interface ESReducer<State, Event> {
    fun State.reduce(event : EventModel<Event>) : State
}