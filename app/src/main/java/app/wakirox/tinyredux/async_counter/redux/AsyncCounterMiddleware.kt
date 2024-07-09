package app.wakirox.tinyredux.async_counter.redux

import android.util.Log
import app.wakirox.redux.AnyMiddleware
import app.wakirox.redux.Middleware
import app.wakirox.redux.RunArguments
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AsyncCounterMiddleware : Middleware<CounterState, CounterActions> {
    override suspend fun execute(args: RunArguments<CounterState, CounterActions>) {
        println("Entering AsyncCounterMiddleware...")

        val (getState, dispatch, next, action) = args

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