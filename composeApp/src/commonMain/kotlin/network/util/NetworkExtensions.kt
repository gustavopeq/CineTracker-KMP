package network.util

import com.projects.cinetracker.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.appendEncodedPathSegments
import io.ktor.http.formUrlEncodeTo
import io.ktor.http.isSuccess
import network.models.ApiError

internal suspend inline fun <reified T> HttpClient.getResult(
    path: String,
): ApiResult<T> {
    return try {
        this.get {
            url {
                appendEncodedPathSegments(path)
            }
        }.asResult()
    } catch (e: Exception) {
        return ApiResult.Error(
            data = ApiError(
                code = e.message,
                exception = e.cause,
            ),
        )
    }
}

internal suspend inline fun <reified T> HttpResponse.asResult(): ApiResult<T> {
    return runCatching<ApiResult<T>> {
        if (this.status.isSuccess()) {
            ApiResult.Success(this.call.response.body(), this.status.value)
        } else {
            val apiError = this.call.body() as ApiError
            ApiResult.Error(apiError, this.status.value)
        }
    }.getOrElse {
        ApiResult.Error(
            ApiError(
                code = it.message,
                exception = it.cause,
            ),
        )
    }
}

internal fun buildUrl(
    vararg path: String?,
    parameterMap: () -> Map<String, String?> = { mapOf() },
): String {
    val result = StringBuilder()
    val existingMap = mutableMapOf<String, String>()

    path.forEach {
        it?.let {
            if (it.isNotEmpty() && !it.startsWith("/")) {
                result.append('/')
            }

            if (it.contains("?")) {
                val urlSplit = it.split("?")
                result.append(urlSplit[0])

                urlSplit[1].split("&").forEach {
                    val keyValue = it.split("=")
                    existingMap[keyValue[0]] = keyValue[1]
                }
            } else {
                result.append(it)
            }
        }
    }

    val filteredMap = parameterMap().filter { !it.value.isNullOrBlank() }.toMutableMap()
    filteredMap += existingMap.filter { !filteredMap.containsKey(it.key) }

    // TODO move api_key to httpClient interceptor
    filteredMap["api_key"] = BuildKonfig.API_KEY

    if (filteredMap.isNotEmpty()) {
        if (!result.endsWith("?")) {
            result.append("?")
        }
        filteredMap
            .map { Pair(it.key, it.value) }
            .toList()
            .formUrlEncodeTo(result)
    }

    return result.toString()
}
