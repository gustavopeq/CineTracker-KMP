package auth.platform

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import auth.model.AuthTokens

actual class TokenStorage(context: Context) {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        PREFS_NAME,
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    actual fun saveTokens(tokens: AuthTokens) {
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, tokens.accessToken)
            .putString(KEY_REFRESH_TOKEN, tokens.refreshToken)
            .putString(KEY_USER_ID, tokens.userId)
            .putString(KEY_DISPLAY_NAME, tokens.displayName)
            .apply()
    }

    actual fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)
    actual fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)
    actual fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)
    actual fun getDisplayName(): String? = prefs.getString(KEY_DISPLAY_NAME, null)

    actual fun clearTokens() {
        prefs.edit().clear().apply()
    }

    actual fun getAuthTokens(): AuthTokens? {
        val accessToken = getAccessToken() ?: return null
        val refreshToken = getRefreshToken() ?: return null
        val userId = getUserId() ?: return null
        val displayName = getDisplayName() ?: return null
        return AuthTokens(accessToken, refreshToken, userId, displayName)
    }

    companion object {
        private const val PREFS_NAME = "cinetracker_auth"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_DISPLAY_NAME = "display_name"
    }
}
