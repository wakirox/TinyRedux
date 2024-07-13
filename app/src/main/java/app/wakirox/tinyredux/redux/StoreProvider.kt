package app.wakirox.tinyredux.redux

import app.wakirox.redux.Store
import app.wakirox.redux.SubStore
import app.wakirox.redux.combine
import app.wakirox.redux.logger
import app.wakirox.redux.pullback
import app.wakirox.tinyredux.ui.counter.redux.CounterActions
import app.wakirox.tinyredux.ui.counter.redux.CounterState
import app.wakirox.tinyredux.ui.counter.redux.counter
import app.wakirox.tinyredux.ui.counter.redux.counterMiddleware
import app.wakirox.tinyredux.ui.counter.redux.counterReducer
import app.wakirox.tinyredux.ui.counter.redux.counterState
import app.wakirox.tinyredux.ui.counter.redux.withCounter
import app.wakirox.tinyredux.ui.text_input.redux.TextActions
import app.wakirox.tinyredux.ui.text_input.redux.TextState
import app.wakirox.tinyredux.ui.text_input.redux.text
import app.wakirox.tinyredux.ui.text_input.redux.textReducer
import app.wakirox.tinyredux.ui.text_input.redux.textState
import app.wakirox.tinyredux.ui.text_input.redux.withText

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
                            updateGlobalState = AppState::withCounter
                        ),
                        pullback(
                            reducer = textReducer,
                            toState = AppState::textState,
                            toAction = AppActions::text,
                            updateGlobalState = AppState::withText
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

        val textStore: SubStore<AppState, AppActions, TextState, TextActions> by lazy {
            SubStore(
                store = defaultStore,
                toLocalState = { it.textState },
                toGlobalAction = { AppActions.Text(it) }
            )
        }
    }
}