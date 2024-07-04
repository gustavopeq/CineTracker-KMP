package common.util

import java.util.Locale

actual object PlatformUtils {
    actual val isIOS: Boolean = false
    actual fun getUserLanguage(): String = Locale.getDefault().language
    actual fun getUserCountry(): String = Locale.getDefault().country
    actual fun getLocale(): String {
        val language = getUserLanguage()
        val country = getUserCountry()

        return "$language-$country"
    }
}
