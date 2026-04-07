package common.util.platform

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.projects.cinetracker.BuildConfig
import java.util.Locale

actual object PlatformUtils {
    actual val isIOS: Boolean = false
    actual val isDebugBuild: Boolean = BuildConfig.DEBUG
    actual fun getUserLanguage(): String = Locale.getDefault().language
    actual fun getUserCountry(): String = Locale.getDefault().country
    actual fun getLocale(): String {
        val language = getUserLanguage()
        val country = getUserCountry()

        return "$language-$country"
    }
    actual fun getDisplayCountry(isoCode: String): String =
        Locale("", isoCode).getDisplayCountry(Locale.getDefault()).ifEmpty { isoCode }
    actual fun applyAppLocale(languageTag: String) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag))
    }
}
