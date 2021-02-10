package me.danielllewellyn.es.internal.util

import me.danielllewellyn.es.Conditional
import me.danielllewellyn.es.interfaces.ESReducer
import me.danielllewellyn.es.model.EventModel

internal class ChainedConditionalEsReducer<State, Event>(private val list: List<Pair<ESReducer<State, Event>, Conditional<Event>>>) :
    ESReducer<State, Event> {

    override fun State.reduce(event: EventModel<Event>): State {
        var baseState: State = this

        for (item in list) {
            baseState = if (item.second(event.value)) {
                item.first.run {
                    baseState.reduce(event)
                }
            } else {
                baseState
            }
        }

        return baseState
    }
}