package app.wakirox.redux

import app.wakirox.redux.action.SomeAction
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
class StoreTests {
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

    @Test
    fun storeTest() = runTest {

        val initialState = SomeState(count = 0, message = "Hello")
        val store: Store<SomeState, SomeAction> = Store(initialState, reducer, emptyList())

        store.dispatch(SomeAction.Increment(5))
        advanceUntilIdle()

        assertEquals(SomeState(count = 5, message = "Hello"), store.state.value)

        store.dispatch(SomeAction.UpdateMessage("Hey"))
        advanceUntilIdle()
        assertEquals(SomeState(count = 5, message = "Hey"), store.state.value)
    }

    @Test
    fun `bind should emit the correct value when state changes`() = runTest {
        val store = Store(SomeState(0, "Initial"), reducer)
        val countFlow = store.bind(SomeState::count) { oldState: SomeState, value: Int ->
            oldState.copy(count = value)
        }

        assertEquals(0, countFlow.get().value)

        store.dispatch(SomeAction.Increment(1))

        advanceUntilIdle()

        assertEquals(1, countFlow.get().value)
        assertEquals(1, store.state.value.count)
    }

    @Test
    fun `reducedBind should dispatch actions when the flow value changes`() = runTest {
        val store = Store(SomeState(0, "Initial"), reducer)
        val nameFlow = store.reducedBind(SomeState::message) { SomeAction.UpdateMessage(it) }

        nameFlow.set("New Name")

        advanceUntilIdle()

        assertEquals("New Name", store.state.value.message)

        store.dispatch(action = SomeAction.UpdateMessage("Some new message"))

        advanceUntilIdle()

        assertEquals("Some new message", nameFlow.get().value)
    }
}