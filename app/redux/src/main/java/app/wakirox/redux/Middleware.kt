package app.wakirox.redux

data class RunArguments<S, A>(
    val getState: () -> S,
    val dispatch: suspend (A) -> Unit,
    val next: suspend (A) -> Unit,
    val action: A
)

interface Middleware<S, A> {
    suspend fun execute(args: RunArguments<S, A>)
}

class AnyMiddleware<S, A>(private val job: suspend (RunArguments<S, A>) -> Unit) : Middleware<S, A> {

    constructor(middleware: Middleware<S, A>) : this(middleware::execute)

    override suspend fun execute(args: RunArguments<S, A>) {
        job(args)
    }

}