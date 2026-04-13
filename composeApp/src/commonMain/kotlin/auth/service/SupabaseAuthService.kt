package auth.service

import auth.model.CloudContentDownload
import auth.model.CloudListDownload
import auth.model.CloudRatingDownload
import auth.model.SupabaseSessionResponse
import auth.model.UploadSnapshotRequest
import auth.model.UserPreferencesDto

sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
}

interface SupabaseAuthService {
    suspend fun signUpWithEmail(
        email: String,
        password: String,
        name: String
    ): AuthResult<SupabaseSessionResponse>

    suspend fun signInWithEmail(
        email: String,
        password: String
    ): AuthResult<SupabaseSessionResponse>

    suspend fun signInWithIdToken(
        provider: String,
        idToken: String,
        nonce: String? = null
    ): AuthResult<SupabaseSessionResponse>

    suspend fun refreshToken(refreshToken: String): AuthResult<SupabaseSessionResponse>
    suspend fun signOut(accessToken: String): AuthResult<Unit>
    suspend fun deleteAccount(accessToken: String): AuthResult<Unit>
    suspend fun resetPassword(email: String): AuthResult<Unit>
    suspend fun updatePassword(accessToken: String, newPassword: String): AuthResult<Unit>
    suspend fun fetchUserPreferences(
        accessToken: String,
        userId: String
    ): AuthResult<List<UserPreferencesDto>>
    suspend fun upsertUserPreferences(
        accessToken: String,
        dto: UserPreferencesDto
    ): AuthResult<Unit>

    suspend fun uploadSnapshot(
        accessToken: String,
        request: UploadSnapshotRequest
    ): AuthResult<Unit>

    suspend fun fetchCloudLists(
        accessToken: String,
        userId: String
    ): AuthResult<List<CloudListDownload>>

    suspend fun fetchCloudContent(
        accessToken: String,
        userId: String
    ): AuthResult<List<CloudContentDownload>>

    suspend fun fetchCloudRatings(
        accessToken: String,
        userId: String
    ): AuthResult<List<CloudRatingDownload>>
}
