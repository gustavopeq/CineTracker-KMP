package auth.platform

import com.projects.cinetracker.BuildKonfig

object GoogleSignInBridge {

    val iosClientId: String get() = BuildKonfig.GOOGLE_IOS_CLIENT_ID
    val webClientId: String get() = BuildKonfig.GOOGLE_WEB_CLIENT_ID

    var onSignInRequest: (() -> Unit)? = null
    internal var onSignInResult: ((idToken: String?, nonce: String?, error: String?) -> Unit)? = null

    fun completeSignIn(idToken: String?, nonce: String?, error: String?) {
        onSignInResult?.invoke(idToken, nonce, error)
        onSignInResult = null
    }
}
