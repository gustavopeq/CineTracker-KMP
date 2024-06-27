package network.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import network.models.ApiError

sealed class ApiResult<T> {
    class Success<T>(val data: T, val code: Int) : ApiResult<T>()
    class Error<T>(val data: ApiError, val code: Int? = null) : ApiResult<T>()
}

fun <T> ApiResult<T>.asFlow(): Flow<Either<T, ApiError>> {
    val result = this
    return flow {
        when (result) {
            is ApiResult.Success -> emit(left(result.data))
            is ApiResult.Error -> emit(right(result.data))
        }
    }
}
