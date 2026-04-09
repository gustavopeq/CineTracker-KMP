package auth.service

import auth.model.SupabaseSessionResponse

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
        idToken: String
    ): AuthResult<SupabaseSessionResponse>

    suspend fun refreshToken(refreshToken: String): AuthResult<SupabaseSessionResponse>
    suspend fun signOut(accessToken: String): AuthResult<Unit>
    suspend fun deleteAccount(accessToken: String): AuthResult<Unit>
    suspend fun resetPassword(email: String): AuthResult<Unit>
}
