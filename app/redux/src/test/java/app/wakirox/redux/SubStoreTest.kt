package app.wakirox.redux

import app.wakirox.redux.action.SomeAction
import app.wakirox.redux.action.SomeLocalAction
import app.wakirox.redux.state.SomeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SubStoreTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private val reducer = Reducer<SomeState, SomeAction> { state, action ->
        when (action) {
            is SomeAction.Increment -> state.copy(count = state.count + action.value)
            is SomeAction.UpdateMessage -> state.copy(message = action.message)
        }
    }
    private val actionMapper: (SomeLocalAction) -> SomeAction = { action ->
        when (action) {
            is SomeLocalAction.Increment -> SomeAction.Increment(action.value)
            is SomeLocalAction.UpdateMessage -> SomeAction.UpdateMessage(action.message)
        }
    }

    @Test
    fun testSubStore() = runTest {

        val store = Store(
            initialState = SomeState(count = 0, message = "Hello"),
            reducer = reducer
        )

        val subStore = SubStore(
            store,
            toLocalState = SomeState::count,
            toGlobalAction = actionMapper
        )

        assertEquals(0, subStore.state.value)

        subStore.dispatch(SomeLocalAction.Increment(5))

        advanceUntilIdle()

        assertEquals(SomeState(count = 5, message = "Hello"), store.state.value)

        assertEquals(5, subStore.state.value)

    }

    @Test
    fun testBinding() = runTest {

        val store = Store(
            initialState = SomeState(count = 0, message = "Hello"),
            reducer = reducer
        )

        val subStore = SubStore(
            store,
            toLocalState = SomeState::count,
            toGlobalAction = actionMapper
        )

        // Test initial state
        advanceUntilIdle()

        // Test binding
        val binding =
            subStore.bind(valueSelector = { it }, stateUpdater = { state, value -> state.copy(count = value) })

        advanceUntilIdle()

        assertEquals(0, binding.get().value)

        // Test reduced binding
        val reducedBinding = subStore.bindWithAction(
            valueSelector = { it },
            action = SomeLocalAction::Increment
        )

        reducedBinding.set(10)

        advanceUntilIdle()

        assertEquals(SomeState(count = 10, message = "Hello"), store.state.value)

        assertEquals(10, subStore.state.value)
    }
}