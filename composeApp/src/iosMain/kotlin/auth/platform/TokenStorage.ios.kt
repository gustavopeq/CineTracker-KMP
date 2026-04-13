package auth.platform

import auth.model.AuthTokens
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual class TokenStorage {
    private val serviceName = "com.projects.cinetracker.auth"

    actual fun saveTokens(tokens: AuthTokens) {
        save(KEY_ACCESS_TOKEN, tokens.accessToken)
        save(KEY_REFRESH_TOKEN, tokens.refreshToken)
        save(KEY_USER_ID, tokens.userId)
        save(KEY_DISPLAY_NAME, tokens.displayName)
    }

    actual fun getAccessToken(): String? = read(KEY_ACCESS_TOKEN)
    actual fun getRefreshToken(): String? = read(KEY_REFRESH_TOKEN)
    actual fun getUserId(): String? = read(KEY_USER_ID)
    actual fun getDisplayName(): String? = read(KEY_DISPLAY_NAME)

    actual fun clearTokens() {
        delete(KEY_ACCESS_TOKEN)
        delete(KEY_REFRESH_TOKEN)
        delete(KEY_USER_ID)
        delete(KEY_DISPLAY_NAME)
    }

    actual fun getAuthTokens(): AuthTokens? {
        val accessToken = getAccessToken() ?: return null
        val refreshToken = getRefreshToken() ?: return null
        val userId = getUserId() ?: return null
        val displayName = getDisplayName() ?: return null
        return AuthTokens(accessToken, refreshToken, userId, displayName)
    }

    private fun save(key: String, value: String) {
        delete(key)
        val valueData = NSString.create(string = value)
            .dataUsingEncoding(NSUTF8StringEncoding) ?: return

        memScoped {
            val query = CFDictionaryCreateMutable(null, 4, null, null)
            val serviceRef = CFBridgingRetain(serviceName)
            val keyRef = CFBridgingRetain(key)
            val dataRef = CFBridgingRetain(valueData)
            try {
                CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
                CFDictionaryAddValue(query, kSecAttrService, serviceRef)
                CFDictionaryAddValue(query, kSecAttrAccount, keyRef)
                CFDictionaryAddValue(query, kSecValueData, dataRef)
                SecItemAdd(query, null)
            } finally {
                CFBridgingRelease(serviceRef)
                CFBridgingRelease(keyRef)
                CFBridgingRelease(dataRef)
            }
        }
    }

    private fun read(key: String): String? {
        memScoped {
            val query = CFDictionaryCreateMutable(null, 5, null, null)
            CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
            CFDictionaryAddValue(query, kSecAttrService, CFBridgingRetain(serviceName))
            CFDictionaryAddValue(query, kSecAttrAccount, CFBridgingRetain(key))
            CFDictionaryAddValue(query, kSecReturnData, kCFBooleanTrue)
            CFDictionaryAddValue(query, kSecMatchLimit, kSecMatchLimitOne)

            val result = alloc<CFTypeRefVar>()
            val status = SecItemCopyMatching(query, result.ptr)

            if (status == errSecSuccess) {
                val data = CFBridgingRelease(result.value) as? NSData ?: return null
                return NSString.create(data = data, encoding = NSUTF8StringEncoding)?.toString()
            }
            return null
        }
    }

    private fun delete(key: String) {
        memScoped {
            val query = CFDictionaryCreateMutable(null, 3, null, null)
            val serviceRef = CFBridgingRetain(serviceName)
            val keyRef = CFBridgingRetain(key)
            try {
                CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
                CFDictionaryAddValue(query, kSecAttrService, serviceRef)
                CFDictionaryAddValue(query, kSecAttrAccount, keyRef)
                SecItemDelete(query)
            } finally {
                CFBridgingRelease(serviceRef)
                CFBridgingRelease(keyRef)
            }
        }
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_DISPLAY_NAME = "display_name"
    }
}
