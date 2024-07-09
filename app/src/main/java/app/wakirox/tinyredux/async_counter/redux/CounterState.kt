package app.wakirox.tinyredux.async_counter.redux

import app.wakirox.tinyredux.redux.AppState

data class CounterState(val counter: Int, val counterMessage: String)

val AppState.counterState : CounterState
    get() = CounterState(counter, counterMessage)
