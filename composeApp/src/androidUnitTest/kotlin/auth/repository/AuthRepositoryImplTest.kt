package auth.repository

import auth.model.AuthState
import auth.model.AuthTokens
import auth.model.SignInResult
import auth.model.SupabaseSessionResponse
import auth.model.SupabaseUser
import auth.model.SupabaseUserMetadata
import auth.platform.PlatformSignInProvider
import auth.platform.TokenStorage
import auth.service.AuthResult
import auth.service.SupabaseAuthService
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
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
        repository = AuthRepositoryImpl(service, tokenStorage, signInProvider)
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
    }

    // endregion

    // region signInWithEmail

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

        val result = repository.signInWithGoogle()

        assertIs<AuthResult.Success<Unit>>(result)
        coVerify { service.signInWithIdToken("google", "google-id-token") }
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

        val result = repository.signInWithGoogle()

        assertIs<AuthResult.Success<Unit>>(result)
        coVerify { service.refreshToken("oauth-refresh") }
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
    }

    // endregion

    // region signOut

    @Test
    fun `signOut clears tokens and emits LoggedOut`() = runTest {
        every { tokenStorage.getAccessToken() } returns "test-access"
        coEvery { service.signOut("test-access") } returns AuthResult.Success(Unit)

        val result = repository.signOut()

        assertIs<AuthResult.Success<Unit>>(result)
        verify { tokenStorage.clearTokens() }
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
    fun `deleteAccount clears tokens on success`() = runTest {
        every { tokenStorage.getAccessToken() } returns "test-access"
        coEvery { service.deleteAccount("test-access") } returns AuthResult.Success(Unit)

        val result = repository.deleteAccount()

        assertIs<AuthResult.Success<Unit>>(result)
        verify { tokenStorage.clearTokens() }
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
}
