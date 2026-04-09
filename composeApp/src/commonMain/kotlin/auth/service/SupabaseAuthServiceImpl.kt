package auth.service

import auth.model.SupabaseEmailSignInRequest
import auth.model.SupabaseErrorResponse
import auth.model.SupabaseIdTokenRequest
import auth.model.SupabaseRefreshRequest
import auth.model.SupabaseSessionResponse
import auth.model.SupabaseSignUpMetadata
import auth.model.SupabaseSignUpRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class SupabaseAuthServiceImpl(private val client: HttpClient) : SupabaseAuthService {

    private val lenientJson = Json { ignoreUnknownKeys = true; isLenient = true }

    override suspend fun signUpWithEmail(
        email: String,
        password: String,
        name: String
    ): AuthResult<SupabaseSessionResponse> = safeCall {
        client.post("auth/v1/signup") {
            setBody(
                SupabaseSignUpRequest(
                    email = email,
                    password = password,
                    data = SupabaseSignUpMetadata(fullName = name)
                )
            )
        }
    }

    override suspend fun signInWithEmail(
        email: String,
        password: String
    ): AuthResult<SupabaseSessionResponse> = safeCall {
        client.post("auth/v1/token?grant_type=password") {
            setBody(SupabaseEmailSignInRequest(email = email, password = password))
        }
    }

    override suspend fun signInWithIdToken(
        provider: String,
        idToken: String
    ): AuthResult<SupabaseSessionResponse> = safeCall {
        client.post("auth/v1/token?grant_type=id_token") {
            setBody(SupabaseIdTokenRequest(provider = provider, idToken = idToken))
        }
    }

    override suspend fun refreshToken(
        refreshToken: String
    ): AuthResult<SupabaseSessionResponse> = safeCall {
        client.post("auth/v1/token?grant_type=refresh_token") {
            setBody(SupabaseRefreshRequest(refreshToken = refreshToken))
        }
    }

    override suspend fun signOut(accessToken: String): AuthResult<Unit> {
        return try {
            val response = client.post("auth/v1/logout") {
                bearerAuth(accessToken)
            }
            if (response.status.isSuccess()) {
                AuthResult.Success(Unit)
            } else {
                val error = parseError(response.bodyAsText())
                AuthResult.Error(error)
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Sign out failed")
        }
    }

    override suspend fun deleteAccount(accessToken: String): AuthResult<Unit> {
        return try {
            val response = client.post("rest/v1/rpc/delete_own_account") {
                bearerAuth(accessToken)
            }
            if (response.status.isSuccess()) {
                AuthResult.Success(Unit)
            } else {
                val error = parseError(response.bodyAsText())
                AuthResult.Error(error)
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Account deletion failed")
        }
    }

    override suspend fun resetPassword(email: String): AuthResult<Unit> {
        return try {
            val response = client.post("auth/v1/recover") {
                setBody(buildJsonObject { put("email", email) })
            }
            if (response.status.isSuccess()) {
                AuthResult.Success(Unit)
            } else {
                val error = parseError(response.bodyAsText())
                AuthResult.Error(error)
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Password reset failed")
        }
    }

    private suspend inline fun <reified T> safeCall(
        block: () -> HttpResponse
    ): AuthResult<T> {
        return try {
            val response = block()
            if (response.status.isSuccess()) {
                AuthResult.Success(response.body<T>())
            } else {
                val error = parseError(response.bodyAsText())
                AuthResult.Error(error)
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Unknown error")
        }
    }

    private fun parseError(body: String): String {
        return try {
            lenientJson.decodeFromString<SupabaseErrorResponse>(body).getErrorMessage()
        } catch (e: Exception) {
            body.ifEmpty { "Unknown error" }
        }
    }
}
