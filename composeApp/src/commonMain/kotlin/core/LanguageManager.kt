package core

import common.util.PlatformUtils.getLocale
import common.util.PlatformUtils.getUserCountry
import common.util.PlatformUtils.getUserLanguage

object LanguageManager {
    private val supportedLanguages = listOf(
        "en-US",
        "en-CA",
        "pt-BR",
        "es-ES",
        "es-MX",
        "es-BO",
        "es-CL",
        "es-CO",
        "es-EC",
        "es-PY",
        "es-PE",
        "es-PR",
        "es-UY",
        "es-VE",
        "es-CR",
    )
    private const val DEFAULT_LANGUAGE = "en-US"

    fun getUserLanguageTag(): String {
        val languageTag = getLocale()
        val language = getUserLanguage()
        println("languageTag: $languageTag - $language")

        return when {
            languageTag.isSupported() -> languageTag
            language == "pt" -> "pt-BR"
            language == "es" -> "es-ES"
            else -> DEFAULT_LANGUAGE
        }
    }

    private fun String.isSupported(): Boolean {
        return supportedLanguages.contains(this)
    }

    fun getUserCountryCode(): String = getUserCountry()
}
