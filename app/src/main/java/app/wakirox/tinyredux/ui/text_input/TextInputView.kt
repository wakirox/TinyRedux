package app.wakirox.tinyredux.ui.text_input

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import app.wakirox.redux.SubStore
import app.wakirox.tinyredux.redux.AppActions
import app.wakirox.tinyredux.redux.AppState
import app.wakirox.tinyredux.ui.text_input.redux.TextActions
import app.wakirox.tinyredux.ui.text_input.redux.TextState

@Composable
fun TextInputViewBind(
    modifier: Modifier = Modifier,
    store: SubStore<AppState, AppActions, TextState, TextActions>,
) {
    val binding = store.bind(TextState::text) { appState, value -> appState.copy(message = value) }

    val text by binding.get().collectAsState()
    Column(modifier = modifier.fillMaxWidth()) {
        Text("[Bind] Input text: $text",modifier = Modifier.fillMaxWidth())
        TextField(
            value = text,
            onValueChange = { binding.set(it) },
            modifier =Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun TextInputViewBindWithAction(
    modifier: Modifier = Modifier,
    store: SubStore<AppState, AppActions, TextState, TextActions>,
) {
    val binding = store.bindWithAction(TextState::text, TextActions::UpdateText)
    val text by binding.get().collectAsState()

    Column(modifier = modifier.fillMaxWidth()) {
        Text("[BindWithAction] Input text: $text",modifier = Modifier.fillMaxWidth())
        TextField(
            value = text,
            onValueChange = { binding.set(it) },
            modifier =Modifier.fillMaxWidth()
        )
    }
}