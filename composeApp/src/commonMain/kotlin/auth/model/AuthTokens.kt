package auth.model

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val displayName: String
)
