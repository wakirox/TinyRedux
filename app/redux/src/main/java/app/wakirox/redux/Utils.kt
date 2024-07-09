package app.wakirox.redux

import android.util.Log

fun <State, Action> combine(vararg reducers: Reducer<State, Action>): Reducer<State, Action> where Action : Any =
    Reducer { state, action ->
        reducers.fold(state) { acc, reducer -> reducer.reduce(acc, action) }
    }

fun <LocalState, GlobalState, LocalAction, GlobalAction> pullback(
    reducer: Reducer<LocalState, LocalAction>,
    toState: (GlobalState) -> LocalState,
    updateGlobalState: (GlobalState, LocalState) -> GlobalState,
    toAction: (GlobalAction) -> LocalAction?
) where LocalAction : Any, GlobalAction : Any = Reducer<GlobalState, GlobalAction> { globalState, globalAction ->
    val localAction: LocalAction = toAction(globalAction) ?: return@Reducer globalState
    val localState = toState(globalState)
    val reducedState = reducer.reduce(localState, localAction)
    //not sure
    updateGlobalState(globalState, reducedState)
}

fun <LocalState, GlobalState, LocalAction, GlobalAction> pullback(
    middleware: AnyMiddleware<LocalState, LocalAction>,
    toState: (GlobalState) -> LocalState,
    toAction: (GlobalAction) -> LocalAction?,
    toGlobalAction: (LocalAction) -> GlobalAction,
) where LocalAction : Any, GlobalAction : Any = AnyMiddleware { runArguments ->
    toAction(runArguments.action)?.let { action ->
        middleware.execute(
            RunArguments(
                getState = { toState(runArguments.getState()) },
                dispatch = { runArguments.dispatch(toGlobalAction(it)) },
                next = { runArguments.next(toGlobalAction(it)) },
                action = action
            )
        )
    } ?: runArguments.next(runArguments.action)
}

fun <State, Action> logger(
    reducer: Reducer<State, Action>,
    actions: Set<Action> = emptySet(),
    completion: (State, Action) -> Unit = ::logHandler,
): Reducer<State, Action> = Reducer { state, action ->
    val outState = reducer.reduce(state, action)
    if (!actions.contains(action)) {
        completion(outState, action)
    }
    outState
}

fun <State, Action> logger(middleware: AnyMiddleware<State, Action>): AnyMiddleware<State, Action> =
    AnyMiddleware(job = { runArguments ->
        //log.. something
        middleware.execute(runArguments)
    })

private fun <State, Action> logHandler(state: State, action: Action) {
    Log.d("Redux", "[[Action]] $action")
}