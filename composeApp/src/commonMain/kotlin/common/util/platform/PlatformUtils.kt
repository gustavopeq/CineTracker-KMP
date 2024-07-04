package common.util.platform

expect object PlatformUtils {
    val isIOS: Boolean
    fun getUserLanguage(): String
    fun getUserCountry(): String
    fun getLocale(): String
}
