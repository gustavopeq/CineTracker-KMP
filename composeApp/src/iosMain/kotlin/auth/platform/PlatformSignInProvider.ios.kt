package auth.platform

import auth.model.SignInResult
import auth.service.AUTH_CALLBACK_URL
import com.projects.cinetracker.BuildKonfig
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AuthenticationServices.ASAuthorization
import platform.AuthenticationServices.ASAuthorizationAppleIDCredential
import platform.AuthenticationServices.ASAuthorizationAppleIDProvider
import platform.AuthenticationServices.ASAuthorizationController
import platform.AuthenticationServices.ASAuthorizationControllerDelegateProtocol
import platform.AuthenticationServices.ASAuthorizationControllerPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASAuthorizationScopeEmail
import platform.AuthenticationServices.ASAuthorizationScopeFullName
import platform.AuthenticationServices.ASPresentationAnchor
import platform.AuthenticationServices.ASWebAuthenticationSession
import platform.Foundation.NSError
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual class PlatformSignInProvider {

    actual suspend fun signInWithApple(): SignInResult = suspendCancellableCoroutine { cont ->
        val provider = ASAuthorizationAppleIDProvider()
        val request = provider.createRequest().apply {
            requestedScopes = listOf(ASAuthorizationScopeFullName, ASAuthorizationScopeEmail)
        }

        val delegate = object : NSObject(),
            ASAuthorizationControllerDelegateProtocol,
            ASAuthorizationControllerPresentationContextProvidingProtocol {

            override fun authorizationController(
                controller: ASAuthorizationController,
                didCompleteWithAuthorization: ASAuthorization
            ) {
                val credential =
                    didCompleteWithAuthorization.credential as? ASAuthorizationAppleIDCredential
                val tokenData = credential?.identityToken
                if (tokenData != null) {
                    val idToken = NSString.create(
                        data = tokenData,
                        encoding = NSUTF8StringEncoding
                    )?.toString()
                    if (idToken != null) {
                        cont.resume(SignInResult.IdToken(token = idToken, provider = "apple"))
                    } else {
                        cont.resumeWithException(Exception("Failed to decode Apple ID token"))
                    }
                } else {
                    cont.resumeWithException(Exception("No identity token from Apple"))
                }
            }

            override fun authorizationController(
                controller: ASAuthorizationController,
                didCompleteWithError: NSError
            ) {
                cont.resumeWithException(Exception(didCompleteWithError.localizedDescription))
            }

            override fun presentationAnchorForAuthorizationController(
                controller: ASAuthorizationController
            ): ASPresentationAnchor {
                return getKeyWindow()
            }
        }

        val controller = ASAuthorizationController(
            authorizationRequests = listOf(request)
        )
        controller.delegate = delegate
        controller.presentationContextProvider = delegate
        controller.performRequests()
    }

    actual suspend fun signInWithGoogle(): SignInResult = suspendCancellableCoroutine { cont ->
        val authUrl = "${BuildKonfig.SUPABASE_URL}/auth/v1/authorize" +
            "?provider=google" +
            "&redirect_to=$AUTH_CALLBACK_URL"

        val url = NSURL(string = authUrl)
        val callbackScheme = "com.projects.cinetracker"

        val session = ASWebAuthenticationSession(
            uRL = url,
            callbackURLScheme = callbackScheme
        ) { callbackUrl, error ->
            if (error != null) {
                cont.resumeWithException(Exception(error.localizedDescription))
                return@ASWebAuthenticationSession
            }
            val fragment = callbackUrl?.fragment
            if (fragment != null) {
                val params = parseFragment(fragment)
                val accessToken = params["access_token"]
                val refreshToken = params["refresh_token"]
                if (accessToken != null && refreshToken != null) {
                    cont.resume(
                        SignInResult.OAuthSession(
                            accessToken = accessToken,
                            refreshToken = refreshToken
                        )
                    )
                } else {
                    cont.resumeWithException(Exception("Missing tokens in callback"))
                }
            } else {
                cont.resumeWithException(Exception("No callback data"))
            }
        }
        session.prefersEphemeralWebBrowserSession = false
        session.start()
    }

    private fun parseFragment(fragment: String): Map<String, String> {
        return fragment.split("&").associate {
            val parts = it.split("=", limit = 2)
            parts[0] to (parts.getOrNull(1) ?: "")
        }
    }

    private fun getKeyWindow(): UIWindow {
        val scene = UIApplication.sharedApplication.connectedScenes
            .firstOrNull { it is UIWindowScene } as? UIWindowScene
        return scene?.keyWindow ?: UIWindow()
    }
}
