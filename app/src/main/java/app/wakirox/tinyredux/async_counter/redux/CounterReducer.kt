package app.wakirox.tinyredux.async_counter.redux

import app.wakirox.redux.Reducer

val counterReducer: Reducer<CounterState, CounterActions> = Reducer { state, action ->
    when (action) {
        is CounterActions.IncreaseCounter -> state.copy(counter = state.counter + 1)
        is CounterActions.DecreaseCounter -> state.copy(counter = state.counter - 1)
        else -> state
    }
}