package common.util.platform

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform
import kotlin.system.exitProcess
import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults
import platform.Foundation.countryCode
import platform.Foundation.currentLocale
import platform.Foundation.localizedStringForCountryCode
import platform.Foundation.preferredLanguages

const val DEFAULT_LANGUAGE = "en"
const val DEFAULT_COUNTRY = "US"

actual object PlatformUtils {
    actual val isIOS: Boolean = true

    @OptIn(ExperimentalNativeApi::class)
    actual val isDebugBuild: Boolean = Platform.isDebugBinary
    actual fun getUserLanguage(): String {
        val languageTag = (NSLocale.preferredLanguages.firstOrNull() as String?)
        return languageTag?.split("-")?.first() ?: DEFAULT_LANGUAGE
    }
    actual fun getUserCountry(): String = NSLocale.currentLocale.countryCode ?: DEFAULT_COUNTRY
    actual fun getLocale(): String {
        val locale = (NSLocale.preferredLanguages.firstOrNull() as String?)
        val language = getUserLanguage()
        val country = getUserCountry()

        return locale ?: "$language-$country"
    }
    actual fun getDisplayCountry(isoCode: String): String {
        val preferredLanguage = (NSLocale.preferredLanguages.firstOrNull() as String?) ?: "en"
        val locale = NSLocale(localeIdentifier = preferredLanguage)
        return locale.localizedStringForCountryCode(isoCode) ?: isoCode
    }
    actual fun applyAppLocale(languageTag: String) {
        NSUserDefaults.standardUserDefaults.setObject(listOf(languageTag), forKey = "AppleLanguages")
        NSUserDefaults.standardUserDefaults.synchronize()
    }
}
