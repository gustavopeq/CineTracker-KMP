package features.settings.domain

import auth.model.AuthState
import auth.repository.AuthRepository
import common.util.platform.PlatformUtils
import database.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

data class LanguageItem(val tag: String, val displayName: String)

data class RegionItem(val code: String, val displayName: String)

class SettingsInteractor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
    private val scope: CoroutineScope
) {

    private val _settingsChanged = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val settingsChanged: SharedFlow<Unit> = _settingsChanged

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
        val previousLanguage = getAppLanguage()
        settingsRepository.setAppLanguage(languageTag)
        if (previousLanguage != languageTag) {
            _settingsChanged.tryEmit(Unit)
            PlatformUtils.applyAppLocale(languageTag)
            syncIfLoggedIn { authRepository.syncPreferenceToRemote(language = languageTag) }
        }
    }

    fun getAppRegion(): String {
        val stored = settingsRepository.getAppRegion()
        if (stored != null) return stored

        val country = PlatformUtils.getUserCountry()
        return if (country in supportedRegionCodes) country else "US"
    }

    fun setAppRegion(regionCode: String) {
        val previousRegion = getAppRegion()
        settingsRepository.setAppRegion(regionCode)
        if (previousRegion != regionCode) {
            _settingsChanged.tryEmit(Unit)
            syncIfLoggedIn { authRepository.syncPreferenceToRemote(region = regionCode) }
        }
    }

    fun getUserAvatar(): String = settingsRepository.getUserAvatar() ?: "anonymous_avatar"

    fun setUserAvatar(avatarKey: String) {
        settingsRepository.setUserAvatar(avatarKey)
        _settingsChanged.tryEmit(Unit)
        syncIfLoggedIn { authRepository.syncPreferenceToRemote(avatarKey = avatarKey) }
    }

    private fun syncIfLoggedIn(action: suspend () -> Unit) {
        if (authRepository.authState.value is AuthState.LoggedIn) {
            scope.launch { action() }
        }
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
            LanguageItem("en-US", "English"),
            LanguageItem("es-ES", "Espa\u00f1ol"),
            LanguageItem("pt-BR", "Portugu\u00eas")
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
