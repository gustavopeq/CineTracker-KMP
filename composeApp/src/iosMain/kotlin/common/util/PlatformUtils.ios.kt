package common.util

import platform.Foundation.NSLocale
import platform.Foundation.countryCode
import platform.Foundation.currentLocale
import platform.Foundation.preferredLanguages

actual object PlatformUtils {
    actual val isIOS: Boolean = true
    actual fun getUserLanguage(): String {
        val languageTag = (NSLocale.preferredLanguages.firstOrNull() as String?)
        return languageTag?.split("-")?.first() ?: DEFAULT_LANGUAGE
    }
    actual fun getUserCountry(): String = NSLocale.currentLocale.countryCode ?: DEFAULT_COUNTRY
    actual fun getLocale(): String {
        val language = getUserLanguage()
        val country = getUserCountry()

        return "$language-$country"
    }
}

const val DEFAULT_LANGUAGE = "en"
const val DEFAULT_COUNTRY = "US"
