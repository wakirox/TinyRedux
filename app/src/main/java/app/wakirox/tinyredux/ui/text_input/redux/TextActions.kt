package app.wakirox.tinyredux.ui.text_input.redux

import app.wakirox.tinyredux.redux.AppActions

sealed class TextActions : AppActions() {
    data class UpdateText(val text: String) : TextActions()
}

var AppActions.text: TextActions?
    get() = when (this) {
        is AppActions.Text -> value
        else -> null
    }
    set(newValue) {
        if (this is AppActions.Text && newValue != null) {
            this.value = newValue
        }
    }