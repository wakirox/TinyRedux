package app.wakirox.tinyredux.ui.text_input.redux

import app.wakirox.redux.Reducer

val textReducer: Reducer<TextState, TextActions> = Reducer { state, action ->
    when (action) {
        is TextActions.UpdateText -> state.copy(text = action.text)
    }
}

