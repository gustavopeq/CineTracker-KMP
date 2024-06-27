package network.util

sealed class Either<out L, out R> {
    val isLeft get() = this is Left<L>
    val isRight get() = this is Right<R>
    companion object
}

data class Left<out L>(val value: L) : Either<L, Nothing>()
data class Right<out R>(val error: R) : Either<Nothing, R>()

fun <L> left(value: L) = Left(value)
fun <R> right(error: R) = Right(error)
