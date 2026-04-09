package auth.platform

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import auth.model.SignInResult
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.projects.cinetracker.BuildKonfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual class PlatformSignInProvider(private val context: Context) {

    actual suspend fun signInWithGoogle(): SignInResult {
        val credentialManager = CredentialManager.create(context)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildKonfig.GOOGLE_WEB_CLIENT_ID)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(context, request)
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
        val idToken = googleIdTokenCredential.idToken

        return SignInResult.IdToken(token = idToken, provider = "google")
    }

    actual suspend fun signInWithApple(): SignInResult {
        val deferred = AuthCallbackHandler.createPendingResult()

        val authUrl = "${BuildKonfig.SUPABASE_URL}/auth/v1/authorize" +
            "?provider=apple" +
            "&redirect_to=com.projects.cinetracker://auth-callback"

        withContext(Dispatchers.Main) {
            val intent = CustomTabsIntent.Builder().build()
            intent.launchUrl(context, Uri.parse(authUrl))
        }

        val fragment = deferred.await()
        val params = parseFragment(fragment)
        val accessToken = params["access_token"]
            ?: throw Exception("Missing access_token in callback")
        val refreshToken = params["refresh_token"]
            ?: throw Exception("Missing refresh_token in callback")

        return SignInResult.OAuthSession(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    private fun parseFragment(fragment: String): Map<String, String> {
        return fragment.split("&").associate {
            val parts = it.split("=", limit = 2)
            parts[0] to (parts.getOrNull(1) ?: "")
        }
    }
}
