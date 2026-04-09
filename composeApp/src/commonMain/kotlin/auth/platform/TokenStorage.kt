package auth.platform

import auth.model.AuthTokens

expect class TokenStorage {
    fun saveTokens(tokens: AuthTokens)
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun getUserId(): String?
    fun getDisplayName(): String?
    fun clearTokens()
    fun getAuthTokens(): AuthTokens?
}
