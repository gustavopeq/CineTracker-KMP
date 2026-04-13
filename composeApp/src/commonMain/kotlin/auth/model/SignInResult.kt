package auth.model

sealed class SignInResult {
    data class IdToken(val token: String, val provider: String, val nonce: String? = null) : SignInResult()
    data class OAuthSession(val accessToken: String, val refreshToken: String) : SignInResult()
}
