package common.util.platform

expect object PlatformUtils {
    val isIOS: Boolean
    val isDebugBuild: Boolean
    fun getUserLanguage(): String
    fun getUserCountry(): String
    fun getLocale(): String
    fun getDisplayCountry(isoCode: String): String
}
