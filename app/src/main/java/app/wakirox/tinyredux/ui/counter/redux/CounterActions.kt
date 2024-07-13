package app.wakirox.tinyredux.ui.counter.redux

import app.wakirox.tinyredux.redux.AppActions

sealed class CounterActions : AppActions() {
    data object Procced : CounterActions()
    data object IncreaseCounter : CounterActions()
    data object DecreaseCounter : CounterActions()
}

var AppActions.counter: CounterActions?
    get() = when (this) {
        is AppActions.Counter -> value
        else -> null
    }
    set(newValue) {
        if (this is AppActions.Counter && newValue != null) {
            this.value = newValue
        }
    }