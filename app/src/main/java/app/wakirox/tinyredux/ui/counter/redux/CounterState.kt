package app.wakirox.tinyredux.ui.counter.redux

import app.wakirox.tinyredux.redux.AppState

data class CounterState(val counter: Int, val counterMessage: String)

val AppState.counterState: CounterState
    get() = CounterState(counter, counterMessage)

fun AppState.withCounter(state: CounterState) =
    this.copy(counter = state.counter, counterMessage = state.counterMessage)
