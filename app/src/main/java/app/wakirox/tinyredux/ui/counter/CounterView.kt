package app.wakirox.tinyredux.ui.counter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.wakirox.redux.SubStore
import app.wakirox.tinyredux.ui.counter.redux.CounterActions
import app.wakirox.tinyredux.ui.counter.redux.CounterState
import app.wakirox.tinyredux.redux.AppActions
import app.wakirox.tinyredux.redux.AppState
import app.wakirox.tinyredux.redux.StoreProvider

@Composable
fun CounterView(
    modifier: Modifier = Modifier,
    store: StoreProvider.CounterStore,
) {
    val counterValue by store.state.collectAsState()
    Column(
        modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Async counter increment test",
            style = MaterialTheme.typography.headlineMedium
        )
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlusButtonView(store = store)
            Text(
                text = "${counterValue.counter}",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(20.dp)
            )
            MinusButtonView(store = store)
        }
    }
}

@Composable
fun PlusButtonView(store: SubStore<AppState, AppActions, CounterState, CounterActions>) {
    IconButton(modifier = Modifier.size(100.dp), onClick = {
        store.dispatch(CounterActions.IncreaseCounter)
    }) {
        Icon(Icons.Default.KeyboardArrowUp, null, modifier = Modifier.size(100.dp))
    }
}

@Composable
fun MinusButtonView(store: SubStore<AppState, AppActions, CounterState, CounterActions>) {
    IconButton(modifier = Modifier.size(100.dp), onClick = {
        store.dispatch(CounterActions.DecreaseCounter)
    }) {
        Icon(Icons.Default.KeyboardArrowDown, null, modifier = Modifier.size(100.dp))
    }
}