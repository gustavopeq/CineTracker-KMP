package auth.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseSessionResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_in") val expiresIn: Long,
    val user: SupabaseUser
)

@Serializable
data class SupabaseUser(
    val id: String,
    val email: String? = null,
    @SerialName("user_metadata") val userMetadata: SupabaseUserMetadata? = null
)

@Serializable
data class SupabaseUserMetadata(@SerialName("full_name") val fullName: String? = null, val name: String? = null) {
    fun getDisplayName(): String = fullName ?: name ?: ""
}

@Serializable
data class SupabaseSignUpRequest(val email: String, val password: String, val data: SupabaseSignUpMetadata? = null)

@Serializable
data class SupabaseSignUpMetadata(@SerialName("full_name") val fullName: String)

@Serializable
data class SupabaseEmailSignInRequest(val email: String, val password: String)

@Serializable
data class SupabaseIdTokenRequest(
    val provider: String,
    @SerialName("id_token") val idToken: String,
    val nonce: String? = null
)

@Serializable
data class SupabaseRefreshRequest(@SerialName("refresh_token") val refreshToken: String)

@Serializable
data class UserPreferencesDto(
    @SerialName("user_id") val userId: String,
    @SerialName("avatar_key") val avatarKey: String,
    @SerialName("app_language") val appLanguage: String? = null,
    @SerialName("app_region") val appRegion: String? = null
)

@Serializable
data class SupabaseErrorResponse(
    val error: String? = null,
    @SerialName("error_description") val errorDescription: String? = null,
    val msg: String? = null,
    val message: String? = null
) {
    fun getErrorMessage(): String = errorDescription ?: message ?: msg ?: error ?: "Unknown error"
}
