package auth.repository

import auth.model.AuthState
import auth.model.AuthTokens
import auth.model.SignInResult
import auth.model.SupabaseSessionResponse
import auth.model.UserPreferencesDto
import auth.platform.PlatformSignInProvider
import auth.platform.TokenStorage
import auth.service.AuthResult
import auth.service.SupabaseAuthService
import auth.service.SyncService
import co.touchlab.kermit.Logger
import common.util.platform.PlatformUtils
import database.repository.SettingsRepository
import features.settings.ui.model.getRandomAvatar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepositoryImpl(
    private val service: SupabaseAuthService,
    private val tokenStorage: TokenStorage,
    private val signInProvider: PlatformSignInProvider,
    private val settingsRepository: SettingsRepository,
    private val syncService: SyncService
) : AuthRepository {

    private val log = Logger.withTag("AuthRepository")
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
                saveSession(result.data)
                createPreferencesOnSignUp(
                    avatarKey = getRandomAvatar(),
                    language = settingsRepository.getAppLanguage() ?: "en-US",
                    region = settingsRepository.getAppRegion() ?: "US"
                )
                emitLoggedIn(result.data)
                syncService.performUpload(result.data.accessToken)
                AuthResult.Success(Unit)
            }
            is AuthResult.Error -> result
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): AuthResult<Unit> {
        return when (val result = service.signInWithEmail(email, password)) {
            is AuthResult.Success -> {
                saveSession(result.data)
                fetchAndApplyPreferences()
                emitLoggedIn(result.data)
                handlePostSignInSync(result.data.accessToken, result.data.user.id)
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
        settingsRepository.clearUserAvatar()
        _authState.value = AuthState.LoggedOut
        return result
    }

    override suspend fun deleteAccount(): AuthResult<Unit> {
        val accessToken = tokenStorage.getAccessToken() ?: ""
        val result = service.deleteAccount(accessToken)
        if (result is AuthResult.Success) {
            tokenStorage.clearTokens()
            settingsRepository.clearUserAvatar()
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

    override suspend fun fetchAndApplyPreferences() {
        val accessToken = tokenStorage.getAccessToken() ?: return
        val userId = tokenStorage.getAuthTokens()?.userId ?: return
        when (val result = service.fetchUserPreferences(accessToken, userId)) {
            is AuthResult.Success -> {
                val prefs = result.data.firstOrNull()
                if (prefs != null) {
                    settingsRepository.setUserAvatar(prefs.avatarKey)
                    prefs.appLanguage?.let { language ->
                        settingsRepository.setAppLanguage(language)
                        PlatformUtils.applyAppLocale(language)
                    }
                    prefs.appRegion?.let { region ->
                        settingsRepository.setAppRegion(region)
                    }
                } else {
                    val currentAvatar = settingsRepository.getUserAvatar() ?: getRandomAvatar()
                    val currentLanguage = settingsRepository.getAppLanguage()
                    val currentRegion = settingsRepository.getAppRegion()
                    createPreferencesOnSignUp(
                        avatarKey = currentAvatar,
                        language = currentLanguage ?: "en-US",
                        region = currentRegion ?: "US"
                    )
                }
            }
            is AuthResult.Error -> {
                log.e { "Failed to fetch preferences: ${result.message}" }
            }
        }
    }

    override suspend fun syncPreferenceToRemote(
        avatarKey: String?,
        language: String?,
        region: String?
    ) {
        val accessToken = tokenStorage.getAccessToken() ?: return
        val userId = tokenStorage.getAuthTokens()?.userId ?: return
        val dto = UserPreferencesDto(
            userId = userId,
            avatarKey = avatarKey ?: settingsRepository.getUserAvatar() ?: "anonymous_avatar",
            appLanguage = language ?: settingsRepository.getAppLanguage(),
            appRegion = region ?: settingsRepository.getAppRegion()
        )
        when (val result = service.upsertUserPreferences(accessToken, dto)) {
            is AuthResult.Success -> log.d { "Preferences synced to remote" }
            is AuthResult.Error -> log.e { "Failed to sync preferences: ${result.message}" }
        }
    }

    override suspend fun createPreferencesOnSignUp(
        avatarKey: String,
        language: String,
        region: String
    ) {
        val accessToken = tokenStorage.getAccessToken() ?: return
        val userId = tokenStorage.getAuthTokens()?.userId ?: return
        settingsRepository.setUserAvatar(avatarKey)
        val dto = UserPreferencesDto(
            userId = userId,
            avatarKey = avatarKey,
            appLanguage = language,
            appRegion = region
        )
        when (val result = service.upsertUserPreferences(accessToken, dto)) {
            is AuthResult.Success -> log.d { "Preferences created on sign-up" }
            is AuthResult.Error -> log.e { "Failed to create preferences: ${result.message}" }
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
                saveSession(sessionResult.data)
                fetchAndApplyPreferences()
                emitLoggedIn(sessionResult.data)
                handlePostSignInSync(sessionResult.data.accessToken, sessionResult.data.user.id)
                AuthResult.Success(Unit)
            }
            is AuthResult.Error -> sessionResult
        }
    }

    private suspend fun handlePostSignInSync(accessToken: String, userId: String) {
        if (syncService.hasCloudData(accessToken, userId)) {
            val result = syncService.performDownload(accessToken, userId)
            if (result is AuthResult.Error) {
                log.e { "Failed to restore cloud data: ${result.message}" }
            }
        } else {
            val result = syncService.performUpload(accessToken)
            if (result is AuthResult.Error) {
                log.e { "Failed to upload local data: ${result.message}" }
            }
        }
    }

    private fun emitLoggedIn(session: SupabaseSessionResponse) {
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
