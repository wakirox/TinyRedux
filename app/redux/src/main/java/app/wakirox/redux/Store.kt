package app.wakirox.redux

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class Store<State, Action>(
    initialState: State,
    private val reducer: Reducer<State, Action>,
    private val middlewares: List<AnyMiddleware<State, Action>> = emptyList(),
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main),
) {

    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val actionQueue = ArrayDeque<Action>()
    private var isProcessing = false

    operator fun <Value> get(selector: (State) -> Value): Value {
        return selector(_state.value)
    }

    fun dispatch(action: Action) {
        actionQueue.addLast(action)
        processActionsIfNeeded()

    }

    fun dispatch(vararg actions: Action) {
        dispatch(actions.toList())
    }

    fun dispatch(actions: List<Action>) {
        actionQueue.addAll(actions)
        processActionsIfNeeded()
    }

    private fun processActionsIfNeeded() {
        if (!isProcessing) {
            isProcessing = true
            scope.launch { // Use coroutine for asynchronous processing
                while (actionQueue.isNotEmpty()) {
                    val action = actionQueue.removeFirst()
                    applyMiddlewares(action) { reducedAction ->
                        reduce(reducedAction)
                    }
                }
                isProcessing = false
            }
        }
    }

    private suspend fun applyMiddlewares(action: Action, reduce: suspend (Action) -> Unit) {
        val resolveMiddlewares = middlewares.reversed()
            .fold(
                initial = reduce,
                operation = { next, middleware ->
                    { action ->
                        middleware.execute(
                            RunArguments(
                                getState = ::getState,
                                dispatch = ::dispatch,
                                next = next,
                                action = action
                            )
                        )
                    }
                }
            )

        resolveMiddlewares(action)
    }

    private fun reduce(action: Action) {
        _state.update { currentState ->
            reducer.reduce(currentState, action)
        }
    }

    private fun getState(): State {
        return _state.value
    }
    fun <T> bind(stateToValue: (State) -> T, stateUpdate: (State, T) -> State): Binding<T> {
        val mutableStateFlow = MutableStateFlow(stateToValue(_state.value))
        scope.launch {
            _state.collect { newState ->
                mutableStateFlow.value = stateToValue(newState)
            }
        }
        return Binding(
            get = { mutableStateFlow.asStateFlow() },
            set = { newValue ->
                scope.launch {
                    _state.value = stateUpdate(_state.value, newValue)
                }
            }
        )
    }

    fun <T> reducedBind(stateToValue: (State) -> T, action: (T) -> Action): Binding<T> {
        val mutableStateFlow = MutableStateFlow(stateToValue(_state.value))
        scope.launch {
            _state.collect { newState ->
                mutableStateFlow.value = stateToValue(newState)
            }
        }
        return Binding(
            get = { mutableStateFlow.asStateFlow() },
            set = { newValue ->
                scope.launch {
                    dispatch(action(newValue))
                }
            }
        )
    }
}


class Binding<T>(
    val get: () -> StateFlow<T>,
    val set: (T) -> Unit
)