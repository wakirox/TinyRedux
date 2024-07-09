package app.wakirox.redux

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class Store<State, Action>(
    initialState: State,
    private val reducer: Reducer<State, Action>,
    private val middlewares: List<AnyMiddleware<State, Action>> = emptyList(),
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main),
) where State : Any {

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

    private suspend fun applyMiddlewares(action: Action, reduce: (Action) -> Unit) {
        val resolveMiddlewares = middlewares.reversed()
            .fold(
                initial = reduce,
                operation = { next, middleware ->
                    { action ->
                        scope.launch {
                            middleware.execute(RunArguments(::getState, ::dispatch, next, action))
                        }
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

    fun <T> bind(callback: (State) -> T): StateFlow<T> {
        return _state.map { callback(it) }
            .stateIn(scope, SharingStarted.Eagerly, callback(_state.value))
    }

    fun <T> reducedBind(callback: (State) -> T, action: (T) -> Action): MutableStateFlow<T> {
        return MutableStateFlow(callback(_state.value)).apply {
            onEach { newValue -> dispatch(action(newValue)) }.launchIn(
                scope
            )
        }
    }
}
