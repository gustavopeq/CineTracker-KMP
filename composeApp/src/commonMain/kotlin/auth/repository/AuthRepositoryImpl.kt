package auth.repository

import auth.model.AuthState
import auth.model.AuthTokens
import auth.model.SupabaseSessionResponse
import auth.model.SignInResult
import auth.platform.PlatformSignInProvider
import auth.platform.TokenStorage
import auth.service.AuthResult
import auth.service.SupabaseAuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepositoryImpl(
    private val service: SupabaseAuthService,
    private val tokenStorage: TokenStorage,
    private val signInProvider: PlatformSignInProvider
) : AuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.LoggedOut)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    override fun restoreSession() {
        val tokens = tokenStorage.getAuthTokens()
        if (tokens != null) {
            _authState.value = AuthState.LoggedIn(
                userId = tokens.userId,
                displayName = tokens.displayName
            )
        } else {
            _authState.value = AuthState.LoggedOut
        }
    }

    override suspend fun signUpWithEmail(
        email: String,
        password: String,
        name: String
    ): AuthResult<Unit> {
        return when (val result = service.signUpWithEmail(email, password, name)) {
            is AuthResult.Success -> {
                saveSessionAndEmitLoggedIn(result.data)
                AuthResult.Success(Unit)
            }
            is AuthResult.Error -> result
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): AuthResult<Unit> {
        return when (val result = service.signInWithEmail(email, password)) {
            is AuthResult.Success -> {
                saveSessionAndEmitLoggedIn(result.data)
                AuthResult.Success(Unit)
            }
            is AuthResult.Error -> result
        }
    }

    override suspend fun signInWithGoogle(): AuthResult<Unit> {
        return try {
            handlePlatformSignIn(signInProvider.signInWithGoogle())
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Google sign-in failed")
        }
    }

    override suspend fun signInWithApple(): AuthResult<Unit> {
        return try {
            handlePlatformSignIn(signInProvider.signInWithApple())
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Apple sign-in failed")
        }
    }

    override suspend fun signOut(): AuthResult<Unit> {
        val accessToken = tokenStorage.getAccessToken() ?: ""
        val result = service.signOut(accessToken)
        tokenStorage.clearTokens()
        _authState.value = AuthState.LoggedOut
        return result
    }

    override suspend fun deleteAccount(): AuthResult<Unit> {
        val accessToken = tokenStorage.getAccessToken() ?: ""
        val result = service.deleteAccount(accessToken)
        if (result is AuthResult.Success) {
            tokenStorage.clearTokens()
            _authState.value = AuthState.LoggedOut
        }
        return result
    }

    override suspend fun resetPassword(email: String): AuthResult<Unit> {
        return service.resetPassword(email)
    }

    override suspend fun refreshTokenIfNeeded(): Boolean {
        val refreshToken = tokenStorage.getRefreshToken() ?: return false
        return when (val result = service.refreshToken(refreshToken)) {
            is AuthResult.Success -> {
                saveSession(result.data)
                true
            }
            is AuthResult.Error -> false
        }
    }

    private suspend fun handlePlatformSignIn(signInResult: SignInResult): AuthResult<Unit> {
        val sessionResult = when (signInResult) {
            is SignInResult.IdToken -> {
                service.signInWithIdToken(signInResult.provider, signInResult.token)
            }
            is SignInResult.OAuthSession -> {
                service.refreshToken(signInResult.refreshToken)
            }
        }
        return when (sessionResult) {
            is AuthResult.Success -> {
                saveSessionAndEmitLoggedIn(sessionResult.data)
                AuthResult.Success(Unit)
            }
            is AuthResult.Error -> sessionResult
        }
    }

    private fun saveSessionAndEmitLoggedIn(session: SupabaseSessionResponse) {
        saveSession(session)
        val displayName = session.user.userMetadata?.getDisplayName() ?: ""
        _authState.value = AuthState.LoggedIn(
            userId = session.user.id,
            displayName = displayName
        )
    }

    private fun saveSession(session: SupabaseSessionResponse) {
        val displayName = session.user.userMetadata?.getDisplayName() ?: ""
        tokenStorage.saveTokens(
            AuthTokens(
                accessToken = session.accessToken,
                refreshToken = session.refreshToken,
                userId = session.user.id,
                displayName = displayName
            )
        )
    }
}
