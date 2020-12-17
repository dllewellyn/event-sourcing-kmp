package me.danielllewellyn.es.internal.util

import me.danielllewellyn.es.ESReducer
import me.danielllewellyn.es.model.EventModel

internal class ChainedEsReducer<State, Event>(private val list: List<ESReducer<State, Event>>) :
    ESReducer<State, Event> {

    override fun State.reduce(event: EventModel<Event>): State {
        var baseState: State = this

        for (item in list) {
            baseState = item.run { baseState.reduce(event) }
        }

        return baseState
    }

}