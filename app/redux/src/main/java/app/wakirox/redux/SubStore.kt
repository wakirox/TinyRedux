package app.wakirox.redux

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

class SubStore<State, Action, LocalState, LocalAction>(
    private val store: Store<State, Action>,
    private val toLocalState: (State) -> LocalState,
    private val toGlobalAction: (LocalAction) -> Action,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) where Action : Any?, LocalAction : Any?, State : Any {

    val state: StateFlow<LocalState> = store.state.map {
        toLocalState(it)
    }.stateIn(scope, SharingStarted.Eagerly, toLocalState(store.state.value))

    operator fun <Value> get(keyPath: (LocalState) -> Value): Value { // Use a function for type safety
        return keyPath(state.value)
    }

    fun dispatch(vararg actions: LocalAction) {
        store.dispatch(actions = actions.map { toGlobalAction(it) })
    }

    fun <Value> bind(
        valueSelector: (LocalState) -> Value,
    ): StateFlow<Value> {
        return state
            .map(valueSelector)
            .stateIn(scope, SharingStarted.Eagerly, valueSelector(state.value)
        )
    }

    fun <Value> bindWithAction(
        valueSelector: (LocalState) -> Value,
        action: (Value) -> LocalAction,
    ): MutableStateFlow<Value> {
        val flow = MutableStateFlow(valueSelector(state.value))
        flow.onEach { newValue -> dispatch(action(newValue)) }.launchIn(scope)
        return flow
    }

}



