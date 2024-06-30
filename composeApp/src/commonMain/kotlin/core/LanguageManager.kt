package core

import common.domain.util.Constants.DEFAULT_LANGUAGE_TAG

object LanguageManager {
    fun getUserLanguageTag(): String {
//        val languageTag = userLocale.value.toLanguageTag()
//        val language = userLocale.value.language
//        return when {
//            languageTag.isSupported() -> languageTag
//            language == "pt" -> "pt-BR"
//            language == "es" -> "es-ES"
//            else -> DEFAULT_LANGUAGE
//        }
        return DEFAULT_LANGUAGE_TAG
    }

    fun getUserCountryCode(): String = "US" // userLocale.value.country
}
