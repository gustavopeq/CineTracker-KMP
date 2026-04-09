package auth.repository

import auth.model.AuthState
import auth.service.AuthResult
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val authState: StateFlow<AuthState>
    suspend fun signUpWithEmail(email: String, password: String, name: String): AuthResult<Unit>
    suspend fun signInWithEmail(email: String, password: String): AuthResult<Unit>
    suspend fun signInWithGoogle(): AuthResult<Unit>
    suspend fun signInWithApple(): AuthResult<Unit>
    suspend fun signOut(): AuthResult<Unit>
    suspend fun deleteAccount(): AuthResult<Unit>
    suspend fun resetPassword(email: String): AuthResult<Unit>
    suspend fun refreshTokenIfNeeded(): Boolean
    fun restoreSession()
}
