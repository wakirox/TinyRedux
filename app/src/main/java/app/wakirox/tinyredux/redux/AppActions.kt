package app.wakirox.tinyredux.redux

import app.wakirox.tinyredux.async_counter.redux.CounterActions

open class AppActions {
    data class Counter(var value: CounterActions) : AppActions()
    // ... other AppActions cases
}