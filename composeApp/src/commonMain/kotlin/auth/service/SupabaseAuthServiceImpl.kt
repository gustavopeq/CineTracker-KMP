package auth.service

import auth.model.CloudContentDownload
import auth.model.CloudListDownload
import auth.model.CloudRatingDownload
import auth.model.SupabaseEmailSignInRequest
import auth.model.SupabaseErrorResponse
import auth.model.SupabaseIdTokenRequest
import auth.model.SupabaseRefreshRequest
import auth.model.SupabaseSessionResponse
import auth.model.SupabaseSignUpMetadata
import auth.model.SupabaseSignUpRequest
import auth.model.UploadSnapshotRequest
import auth.model.UserPreferencesDto
import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal const val AUTH_CALLBACK_URL = "com.projects.cinetracker://auth-callback"

class SupabaseAuthServiceImpl(private val client: HttpClient) : SupabaseAuthService {

    private val log = Logger.withTag("SupabaseAuth")
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
        idToken: String,
        nonce: String?
    ): AuthResult<SupabaseSessionResponse> = safeCall {
        client.post("auth/v1/token?grant_type=id_token") {
            setBody(SupabaseIdTokenRequest(provider = provider, idToken = idToken, nonce = nonce))
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
                parameter("redirect_to", AUTH_CALLBACK_URL)
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

    override suspend fun updatePassword(
        accessToken: String,
        newPassword: String
    ): AuthResult<Unit> {
        return try {
            val response = client.put("auth/v1/user") {
                bearerAuth(accessToken)
                setBody(buildJsonObject { put("password", newPassword) })
            }
            if (response.status.isSuccess()) {
                AuthResult.Success(Unit)
            } else {
                val error = parseError(response.bodyAsText())
                AuthResult.Error(error)
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Password update failed")
        }
    }

    override suspend fun fetchUserPreferences(
        accessToken: String,
        userId: String
    ): AuthResult<List<UserPreferencesDto>> = safeCall {
        client.get("rest/v1/user_preferences?user_id=eq.$userId&select=*") {
            bearerAuth(accessToken)
        }
    }

    override suspend fun upsertUserPreferences(
        accessToken: String,
        dto: UserPreferencesDto
    ): AuthResult<Unit> {
        return try {
            val response = client.post("rest/v1/user_preferences") {
                bearerAuth(accessToken)
                header("Prefer", "resolution=merge-duplicates, return=minimal")
                setBody(dto)
            }
            if (response.status.isSuccess()) {
                AuthResult.Success(Unit)
            } else {
                val error = parseError(response.bodyAsText())
                AuthResult.Error(error)
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to save preferences")
        }
    }

    override suspend fun uploadSnapshot(
        accessToken: String,
        request: UploadSnapshotRequest
    ): AuthResult<Unit> {
        return try {
            val response = client.post("rest/v1/rpc/upload_snapshot") {
                bearerAuth(accessToken)
                setBody(request)
            }
            if (response.status.isSuccess()) {
                AuthResult.Success(Unit)
            } else {
                val error = parseError(response.bodyAsText())
                AuthResult.Error(error)
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Upload snapshot failed")
        }
    }

    override suspend fun fetchCloudLists(
        accessToken: String,
        userId: String
    ): AuthResult<List<CloudListDownload>> = safeCall {
        client.get("rest/v1/cloud_lists?user_id=eq.$userId&select=*") {
            bearerAuth(accessToken)
        }
    }

    override suspend fun fetchCloudContent(
        accessToken: String,
        userId: String
    ): AuthResult<List<CloudContentDownload>> = safeCall {
        client.get("rest/v1/cloud_content?user_id=eq.$userId&select=*") {
            bearerAuth(accessToken)
        }
    }

    override suspend fun fetchCloudRatings(
        accessToken: String,
        userId: String
    ): AuthResult<List<CloudRatingDownload>> = safeCall {
        client.get("rest/v1/cloud_ratings?user_id=eq.$userId&select=*") {
            bearerAuth(accessToken)
        }
    }

    private suspend inline fun <reified T> safeCall(
        block: () -> HttpResponse
    ): AuthResult<T> {
        return try {
            val response = block()
            if (response.status.isSuccess()) {
                log.d { "Auth request succeeded: ${response.status}" }
                AuthResult.Success(response.body<T>())
            } else {
                val body = response.bodyAsText()
                log.e { "Auth request failed: ${response.status} - $body" }
                val error = parseError(body)
                AuthResult.Error(error)
            }
        } catch (e: Exception) {
            log.e(e) { "Auth request exception: ${e.message}" }
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
