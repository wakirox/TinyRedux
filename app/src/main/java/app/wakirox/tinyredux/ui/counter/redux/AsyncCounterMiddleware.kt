package app.wakirox.tinyredux.ui.counter.redux

import android.util.Log
import app.wakirox.redux.AnyMiddleware
import app.wakirox.redux.Middleware
import app.wakirox.redux.RunArguments
import kotlinx.coroutines.delay

class AsyncCounterMiddleware : Middleware<CounterState, CounterActions> {
    override suspend fun execute(args: RunArguments<CounterState, CounterActions>) {
        println("Entering AsyncCounterMiddleware...")

        val (getState, _, next, action) = args

        when(action){
            CounterActions.IncreaseCounter -> {
                delay(1000)
                next(action)
                return
            }
            CounterActions.DecreaseCounter -> if(getState().counter == 0){
                Log.d("Redux", "State counter is already 0.")
                return
            }
            else -> {

            }
        }
        next(action)
    }
}

val counterMiddleware = AnyMiddleware(AsyncCounterMiddleware())