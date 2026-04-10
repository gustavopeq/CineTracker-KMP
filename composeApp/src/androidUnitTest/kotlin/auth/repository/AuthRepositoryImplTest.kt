package auth.repository

import auth.model.AuthState
import auth.model.AuthTokens
import auth.model.SignInResult
import auth.model.SupabaseSessionResponse
import auth.model.SupabaseUser
import auth.model.SupabaseUserMetadata
import auth.model.UserPreferencesDto
import auth.platform.PlatformSignInProvider
import auth.platform.TokenStorage
import auth.service.AuthResult
import auth.service.SupabaseAuthService
import auth.service.SyncService
import common.util.platform.PlatformUtils
import database.repository.SettingsRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkAll
import io.mockk.verify
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class AuthRepositoryImplTest {

    private val service: SupabaseAuthService = mockk()
    private val tokenStorage: TokenStorage = mockk(relaxUnitFun = true)
    private val signInProvider: PlatformSignInProvider = mockk()
    private val settingsRepository: SettingsRepository = mockk(relaxUnitFun = true)
    private val syncService: SyncService = mockk(relaxUnitFun = true)

    private lateinit var repository: AuthRepositoryImpl

    private val testSession = SupabaseSessionResponse(
        accessToken = "test-access",
        refreshToken = "test-refresh",
        expiresIn = 3600,
        user = SupabaseUser(
            id = "user-123",
            email = "test@test.com",
            userMetadata = SupabaseUserMetadata(fullName = "Test User")
        )
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkObject(PlatformUtils)
        every { PlatformUtils.applyAppLocale(any()) } just runs
        mockkStatic("features.settings.ui.model.AvatarItemKt")
        repository = AuthRepositoryImpl(service, tokenStorage, signInProvider, settingsRepository, syncService)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // region restoreSession

    @Test
    fun `restoreSession emits LoggedIn when tokens exist`() {
        val tokens = AuthTokens(
            accessToken = "access",
            refreshToken = "refresh",
            userId = "user-123",
            displayName = "Test User"
        )
        every { tokenStorage.getAuthTokens() } returns tokens

        repository.restoreSession()

        val state = repository.authState.value
        assertIs<AuthState.LoggedIn>(state)
        assertEquals("user-123", state.userId)
        assertEquals("Test User", state.displayName)
    }

    @Test
    fun `restoreSession emits LoggedOut when no tokens`() {
        every { tokenStorage.getAuthTokens() } returns null

        repository.restoreSession()

        assertIs<AuthState.LoggedOut>(repository.authState.value)
    }

    // endregion

    // region signUpWithEmail

    @Test
    fun `signUpWithEmail stores tokens and emits LoggedIn on success`() = runTest {
        coEvery {
            service.signUpWithEmail("test@test.com", "password", "Test User")
        } returns AuthResult.Success(testSession)
        every { tokenStorage.getAccessToken() } returns "test-access"
        every { tokenStorage.getAuthTokens() } returns AuthTokens(
            accessToken = "test-access",
            refreshToken = "test-refresh",
            userId = "user-123",
            displayName = "Test User"
        )
        every { settingsRepository.getAppLanguage() } returns "en-US"
        every { settingsRepository.getAppRegion() } returns "US"
        coEvery { service.upsertUserPreferences(any(), any()) } returns AuthResult.Success(Unit)

        val result = repository.signUpWithEmail("test@test.com", "password", "Test User")

        assertIs<AuthResult.Success<Unit>>(result)
        verify {
            tokenStorage.saveTokens(
                AuthTokens(
                    accessToken = "test-access",
                    refreshToken = "test-refresh",
                    userId = "user-123",
                    displayName = "Test User"
                )
            )
        }
        val state = repository.authState.value
        assertIs<AuthState.LoggedIn>(state)
        assertEquals("user-123", state.userId)
        assertEquals("Test User", state.displayName)
        verify { settingsRepository.setUserAvatar(any()) }
    }

    // endregion

    // region signInWithEmail

    @Test
    fun `signInWithEmail fetches and applies preferences on success`() = runTest {
        val prefsDto = UserPreferencesDto(
            userId = "user-123",
            avatarKey = "boy_avatar_2",
            appLanguage = "pt-BR",
            appRegion = "BR"
        )
        coEvery {
            service.signInWithEmail("test@test.com", "password")
        } returns AuthResult.Success(testSession)
        every { tokenStorage.getAccessToken() } returns "test-access"
        every { tokenStorage.getAuthTokens() } returns AuthTokens(
            accessToken = "test-access",
            refreshToken = "test-refresh",
            userId = "user-123",
            displayName = "Test User"
        )
        coEvery {
            service.fetchUserPreferences("test-access", "user-123")
        } returns AuthResult.Success(listOf(prefsDto))

        val result = repository.signInWithEmail("test@test.com", "password")

        assertIs<AuthResult.Success<Unit>>(result)
        verify { settingsRepository.setUserAvatar("boy_avatar_2") }
        verify { settingsRepository.setAppLanguage("pt-BR") }
        verify { settingsRepository.setAppRegion("BR") }
    }

    @Test
    fun `signInWithEmail returns error on failure`() = runTest {
        coEvery {
            service.signInWithEmail("test@test.com", "wrong")
        } returns AuthResult.Error("Invalid credentials")

        val result = repository.signInWithEmail("test@test.com", "wrong")

        assertIs<AuthResult.Error>(result)
        assertEquals("Invalid credentials", result.message)
        assertIs<AuthState.LoggedOut>(repository.authState.value)
    }

    // endregion

    // region signInWithGoogle

    @Test
    fun `signInWithGoogle exchanges id token with supabase`() = runTest {
        coEvery {
            signInProvider.signInWithGoogle()
        } returns SignInResult.IdToken(token = "google-id-token", provider = "google")
        coEvery {
            service.signInWithIdToken("google", "google-id-token")
        } returns AuthResult.Success(testSession)
        every { tokenStorage.getAccessToken() } returns "test-access"
        every { tokenStorage.getAuthTokens() } returns AuthTokens(
            accessToken = "test-access",
            refreshToken = "test-refresh",
            userId = "user-123",
            displayName = "Test User"
        )
        coEvery {
            service.fetchUserPreferences("test-access", "user-123")
        } returns AuthResult.Success(emptyList())
        every { settingsRepository.getUserAvatar() } returns null
        every { settingsRepository.getAppLanguage() } returns "en-US"
        every { settingsRepository.getAppRegion() } returns "US"
        coEvery { service.upsertUserPreferences(any(), any()) } returns AuthResult.Success(Unit)

        val result = repository.signInWithGoogle()

        assertIs<AuthResult.Success<Unit>>(result)
        coVerify { service.signInWithIdToken("google", "google-id-token") }
        val state = repository.authState.value
        assertIs<AuthState.LoggedIn>(state)
    }

    @Test
    fun `signInWithGoogle refreshes OAuth session to get user info`() = runTest {
        coEvery {
            signInProvider.signInWithGoogle()
        } returns SignInResult.OAuthSession(
            accessToken = "oauth-access",
            refreshToken = "oauth-refresh"
        )
        coEvery {
            service.refreshToken("oauth-refresh")
        } returns AuthResult.Success(testSession)
        every { tokenStorage.getAccessToken() } returns "test-access"
        every { tokenStorage.getAuthTokens() } returns AuthTokens(
            accessToken = "test-access",
            refreshToken = "test-refresh",
            userId = "user-123",
            displayName = "Test User"
        )
        coEvery {
            service.fetchUserPreferences("test-access", "user-123")
        } returns AuthResult.Success(emptyList())
        every { settingsRepository.getUserAvatar() } returns null
        every { settingsRepository.getAppLanguage() } returns "en-US"
        every { settingsRepository.getAppRegion() } returns "US"
        coEvery { service.upsertUserPreferences(any(), any()) } returns AuthResult.Success(Unit)

        val result = repository.signInWithGoogle()

        assertIs<AuthResult.Success<Unit>>(result)
        coVerify { service.refreshToken("oauth-refresh") }
        val state = repository.authState.value
        assertIs<AuthState.LoggedIn>(state)
    }

    @Test
    fun `signInWithGoogle returns error when provider throws exception`() = runTest {
        coEvery {
            signInProvider.signInWithGoogle()
        } throws Exception("No credentials available")

        val result = repository.signInWithGoogle()

        assertIs<AuthResult.Error>(result)
        assertEquals("No credentials available", result.message)
        assertIs<AuthState.LoggedOut>(repository.authState.value)
    }

    // endregion

    // region signInWithApple

    @Test
    fun `signInWithApple returns error when provider throws exception`() = runTest {
        coEvery {
            signInProvider.signInWithApple()
        } throws Exception("Apple sign-in cancelled")

        val result = repository.signInWithApple()

        assertIs<AuthResult.Error>(result)
        assertEquals("Apple sign-in cancelled", result.message)
        assertIs<AuthState.LoggedOut>(repository.authState.value)
    }

    // endregion

    // region signOut

    @Test
    fun `signOut clears tokens and avatar and emits LoggedOut`() = runTest {
        every { tokenStorage.getAccessToken() } returns "test-access"
        coEvery { service.signOut("test-access") } returns AuthResult.Success(Unit)

        val result = repository.signOut()

        assertIs<AuthResult.Success<Unit>>(result)
        verify { tokenStorage.clearTokens() }
        verify { settingsRepository.clearUserAvatar() }
        assertIs<AuthState.LoggedOut>(repository.authState.value)
    }

    @Test
    fun `signOut clears tokens even on server error`() = runTest {
        every { tokenStorage.getAccessToken() } returns "test-access"
        coEvery { service.signOut("test-access") } returns AuthResult.Error("Server error")

        repository.signOut()

        verify { tokenStorage.clearTokens() }
        assertIs<AuthState.LoggedOut>(repository.authState.value)
    }

    // endregion

    // region deleteAccount

    @Test
    fun `deleteAccount clears tokens and avatar on success`() = runTest {
        every { tokenStorage.getAccessToken() } returns "test-access"
        coEvery { service.deleteAccount("test-access") } returns AuthResult.Success(Unit)

        val result = repository.deleteAccount()

        assertIs<AuthResult.Success<Unit>>(result)
        verify { tokenStorage.clearTokens() }
        verify { settingsRepository.clearUserAvatar() }
        assertIs<AuthState.LoggedOut>(repository.authState.value)
    }

    @Test
    fun `deleteAccount does not clear tokens on failure`() = runTest {
        every { tokenStorage.getAccessToken() } returns "test-access"
        coEvery { service.deleteAccount("test-access") } returns AuthResult.Error("Forbidden")

        val result = repository.deleteAccount()

        assertIs<AuthResult.Error>(result)
        verify(exactly = 0) { tokenStorage.clearTokens() }
    }

    // endregion

    // region refreshTokenIfNeeded

    @Test
    fun `refreshTokenIfNeeded refreshes and stores new tokens`() = runTest {
        every { tokenStorage.getRefreshToken() } returns "old-refresh"
        coEvery { service.refreshToken("old-refresh") } returns AuthResult.Success(testSession)

        val result = repository.refreshTokenIfNeeded()

        assertTrue(result)
        verify {
            tokenStorage.saveTokens(
                AuthTokens(
                    accessToken = "test-access",
                    refreshToken = "test-refresh",
                    userId = "user-123",
                    displayName = "Test User"
                )
            )
        }
    }

    @Test
    fun `refreshTokenIfNeeded returns false when no refresh token`() = runTest {
        every { tokenStorage.getRefreshToken() } returns null

        val result = repository.refreshTokenIfNeeded()

        assertFalse(result)
    }

    // endregion

    // region fetchAndApplyPreferences

    @Test
    fun `fetchAndApplyPreferences applies remote values locally`() = runTest {
        every { tokenStorage.getAccessToken() } returns "test-access"
        every { tokenStorage.getAuthTokens() } returns AuthTokens(
            accessToken = "test-access",
            refreshToken = "test-refresh",
            userId = "user-123",
            displayName = "Test User"
        )
        val prefs = UserPreferencesDto(
            userId = "user-123",
            avatarKey = "girl_avatar_3",
            appLanguage = "es-ES",
            appRegion = "ES"
        )
        coEvery {
            service.fetchUserPreferences("test-access", "user-123")
        } returns AuthResult.Success(listOf(prefs))

        repository.fetchAndApplyPreferences()

        verify { settingsRepository.setUserAvatar("girl_avatar_3") }
        verify { settingsRepository.setAppLanguage("es-ES") }
        verify { settingsRepository.setAppRegion("ES") }
        verify { PlatformUtils.applyAppLocale("es-ES") }
    }

    @Test
    fun `fetchAndApplyPreferences creates row with random avatar when none exists`() = runTest {
        every { tokenStorage.getAccessToken() } returns "test-access"
        every { tokenStorage.getAuthTokens() } returns AuthTokens(
            accessToken = "test-access",
            refreshToken = "test-refresh",
            userId = "user-123",
            displayName = "Test User"
        )
        coEvery {
            service.fetchUserPreferences("test-access", "user-123")
        } returns AuthResult.Success(emptyList())
        every { settingsRepository.getUserAvatar() } returns null
        every { settingsRepository.getAppLanguage() } returns "pt-BR"
        every { settingsRepository.getAppRegion() } returns "BR"
        coEvery { service.upsertUserPreferences(any(), any()) } returns AuthResult.Success(Unit)

        repository.fetchAndApplyPreferences()

        coVerify {
            service.upsertUserPreferences(
                "test-access",
                match { dto ->
                    dto.userId == "user-123" &&
                        dto.avatarKey.startsWith("boy_avatar_") ||
                        dto.avatarKey.startsWith("girl_avatar_") &&
                        dto.appLanguage == "pt-BR" &&
                        dto.appRegion == "BR"
                }
            )
        }
    }

    // endregion

    // region syncPreferenceToRemote

    @Test
    fun `syncPreferenceToRemote sends correct dto`() = runTest {
        every { tokenStorage.getAccessToken() } returns "test-access"
        every { tokenStorage.getAuthTokens() } returns AuthTokens(
            accessToken = "test-access",
            refreshToken = "test-refresh",
            userId = "user-123",
            displayName = "Test User"
        )
        every { settingsRepository.getUserAvatar() } returns "boy_avatar_1"
        every { settingsRepository.getAppLanguage() } returns "en-US"
        every { settingsRepository.getAppRegion() } returns "US"
        coEvery { service.upsertUserPreferences(any(), any()) } returns AuthResult.Success(Unit)

        repository.syncPreferenceToRemote(language = "pt-BR")

        coVerify {
            service.upsertUserPreferences(
                "test-access",
                UserPreferencesDto(
                    userId = "user-123",
                    avatarKey = "boy_avatar_1",
                    appLanguage = "pt-BR",
                    appRegion = "US"
                )
            )
        }
    }

    // endregion
}
