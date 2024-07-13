package app.wakirox.tinyredux.redux

import app.wakirox.tinyredux.ui.counter.redux.CounterActions
import app.wakirox.tinyredux.ui.text_input.redux.TextActions

open class AppActions {
    data class Counter(var value: CounterActions) : AppActions()
    data class Text(var value: TextActions) : AppActions()
    // ... other AppActions cases
}