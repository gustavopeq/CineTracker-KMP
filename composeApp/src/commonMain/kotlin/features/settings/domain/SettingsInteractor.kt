package features.settings.domain

import common.util.platform.PlatformUtils
import database.repository.SettingsRepository

data class LanguageItem(val tag: String, val displayName: String)

data class RegionItem(val code: String, val displayName: String)

class SettingsInteractor(private val settingsRepository: SettingsRepository) {

    fun getAppLanguage(): String {
        val stored = settingsRepository.getAppLanguage()
        if (stored != null) return stored

        val locale = PlatformUtils.getLocale()
        if (locale in supportedLanguageTags) return locale

        val language = PlatformUtils.getUserLanguage()
        return when (language) {
            "pt" -> "pt-BR"
            "es" -> "es-ES"
            else -> "en-US"
        }
    }

    fun setAppLanguage(languageTag: String) {
        settingsRepository.setAppLanguage(languageTag)
    }

    fun getAppRegion(): String {
        val stored = settingsRepository.getAppRegion()
        if (stored != null) return stored

        val country = PlatformUtils.getUserCountry()
        return if (country in supportedRegionCodes) country else "US"
    }

    fun setAppRegion(regionCode: String) {
        settingsRepository.setAppRegion(regionCode)
    }

    fun getSupportedLanguages(): List<LanguageItem> = SUPPORTED_LANGUAGES

    fun getSupportedRegions(): List<RegionItem> = supportedRegionCodes.map { code ->
        RegionItem(
            code = code,
            displayName = PlatformUtils.getDisplayCountry(code)
        )
    }.sortedBy { it.displayName }

    fun areNotificationsEnabled(): Boolean = settingsRepository.areEngagementRemindersEnabled()

    fun setNotificationsEnabled(enabled: Boolean) {
        settingsRepository.setEngagementRemindersEnabled(enabled)
    }

    companion object {
        private val SUPPORTED_LANGUAGES = listOf(
            LanguageItem("en-US", "English (US)"),
            LanguageItem("en-CA", "English (Canada)"),
            LanguageItem("pt-BR", "Portugu\u00eas (Brasil)"),
            LanguageItem("es-ES", "Espa\u00f1ol (Espa\u00f1a)"),
            LanguageItem("es-MX", "Espa\u00f1ol (M\u00e9xico)"),
            LanguageItem("es-BO", "Espa\u00f1ol (Bolivia)"),
            LanguageItem("es-CL", "Espa\u00f1ol (Chile)"),
            LanguageItem("es-CO", "Espa\u00f1ol (Colombia)"),
            LanguageItem("es-EC", "Espa\u00f1ol (Ecuador)"),
            LanguageItem("es-PY", "Espa\u00f1ol (Paraguay)"),
            LanguageItem("es-PE", "Espa\u00f1ol (Per\u00fa)"),
            LanguageItem("es-PR", "Espa\u00f1ol (Puerto Rico)"),
            LanguageItem("es-UY", "Espa\u00f1ol (Uruguay)"),
            LanguageItem("es-VE", "Espa\u00f1ol (Venezuela)"),
            LanguageItem("es-CR", "Espa\u00f1ol (Costa Rica)")
        )

        private val supportedLanguageTags = SUPPORTED_LANGUAGES.map { it.tag }.toSet()

        private val supportedRegionCodes = listOf(
            "US", "CA", "GB", "IE", "AU", "NZ",
            "BR", "PT", "ES", "MX", "AR", "CL", "CO", "PE", "VE", "EC", "BO", "PY", "UY", "CR", "PR",
            "FR", "DE", "IT", "NL", "BE", "AT", "CH",
            "JP", "KR", "IN", "CN",
            "SE", "NO", "DK", "FI",
            "PL", "RU", "ZA", "TR"
        )
    }
}
