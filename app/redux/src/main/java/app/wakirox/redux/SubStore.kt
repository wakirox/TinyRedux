package app.wakirox.redux

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SubStore<State, Action, LocalState, LocalAction>(
    private val store: Store<State, Action>,
    private val toLocalState: (State) -> LocalState,
    private val toGlobalAction: (LocalAction) -> Action,
    scope: CoroutineScope = CoroutineScope(Dispatchers.Main),
) {

    val state: StateFlow<LocalState> = store.state.map {
        toLocalState(it)
    }.stateIn(scope, SharingStarted.Eagerly, toLocalState(store.state.value))

    operator fun <Value> get(keyPath: (LocalState) -> Value): Value { // Use a function for type safety
        return keyPath(state.value)
    }

    fun dispatch(vararg actions: LocalAction) {
        store.dispatch(actions = actions.map { toGlobalAction(it) })
    }

    fun <T> bind(valueSelector: (LocalState) -> T, stateUpdater: (State, T) -> State): Binding<T> {
        return store.bind(
            valueSelector = { state -> valueSelector(toLocalState(state)) },
            stateUpdater = stateUpdater
        )
    }

    fun <T> bindWithAction(valueSelector: (LocalState) -> T, action: (T) -> LocalAction): Binding<T> {
        return store.reducedBind(
            valueSelector = { state -> valueSelector(toLocalState(state)) },
            action = { toGlobalAction(action(it)) }
        )
    }

}



