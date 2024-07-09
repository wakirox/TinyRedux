package app.wakirox.tinyredux.redux

import app.wakirox.redux.Store
import app.wakirox.redux.SubStore
import app.wakirox.redux.combine
import app.wakirox.redux.logger
import app.wakirox.redux.pullback
import app.wakirox.tinyredux.async_counter.redux.CounterActions
import app.wakirox.tinyredux.async_counter.redux.CounterState
import app.wakirox.tinyredux.async_counter.redux.counter
import app.wakirox.tinyredux.async_counter.redux.counterMiddleware
import app.wakirox.tinyredux.async_counter.redux.counterReducer
import app.wakirox.tinyredux.async_counter.redux.counterState

class StoreProvider {
    companion object {
        val defaultStore: Store<AppState, AppActions> by lazy {
            Store(
                initialState = AppState.default(),
                reducer = logger(
                    combine(
                        pullback(
                            reducer = counterReducer,
                            toState = AppState::counterState,
                            toAction = AppActions::counter,
                            updateGlobalState = { globalState, localState ->
                                globalState.copy(
                                    counter = localState.counter,
                                    counterMessage = localState.counterMessage
                                )
                            }
                        )
                    )
                ),
                middlewares = listOf(
                    pullback(
                        middleware = counterMiddleware,
                        toState = AppState::counterState,
                        toAction = AppActions::counter,
                        toGlobalAction = {
                            AppActions.Counter(it)
                        }
                    )
                )
            )
        }

        val counterStore: SubStore<AppState, AppActions, CounterState, CounterActions> by lazy {
            SubStore(
                store = defaultStore,
                toLocalState = { it.counterState },
                toGlobalAction = { AppActions.Counter(it) }
            )
        }
    }
}