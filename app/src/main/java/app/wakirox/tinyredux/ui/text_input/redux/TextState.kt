package app.wakirox.tinyredux.ui.text_input.redux

import app.wakirox.tinyredux.redux.AppState
import app.wakirox.tinyredux.ui.counter.redux.CounterState

data class TextState(val text: String)

val AppState.textState : TextState
    get() = TextState(message)

fun AppState.withText(state: TextState) =
    this.copy(message = state.text)