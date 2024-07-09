package app.wakirox.redux

//We don't have inout in kotlin
class Reducer<S, A>(inline val reduce : (state : S, action : A) ->  S)
