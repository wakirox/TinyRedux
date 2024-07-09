package app.wakirox.redux.action

sealed class SomeAction {
    data class Increment(val value: Int) : SomeAction()
    data class UpdateMessage(val message: String) : SomeAction()
}

sealed class SomeLocalAction {
    data class Increment(val value: Int) : SomeLocalAction()
    data class UpdateMessage(val message: String) : SomeLocalAction()
}