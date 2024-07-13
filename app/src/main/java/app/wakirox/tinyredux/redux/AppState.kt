package app.wakirox.tinyredux.redux

data class AppState(
    val header: String,
    val message: String,
    val counter: Int,
    val counterMessage: String,
    val timeCount: Int,
    val timerIsRunning: Boolean
) {



    companion object {
        fun default() = AppState(
            header = "Lorem ipsum",
            message = "",
            counter = 0,
            counterMessage = "",
            timeCount = 0,
            timerIsRunning = false
        )
    }


}
