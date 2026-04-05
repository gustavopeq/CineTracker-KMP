
import androidx.compose.ui.window.ComposeUIViewController
import com.projects.cinetracker.BuildKonfig
import common.util.platform.AppHaptics
import common.util.platform.AppNotifications
import common.util.platform.PlatformUtils
import core.di.KoinInitializer
import database.repository.SettingsRepository
import io.sentry.kotlin.multiplatform.Sentry
import org.koin.mp.KoinPlatform

fun mainViewController() = ComposeUIViewController(
    configure = {
        KoinInitializer().init()
        initSentry()
        AppHaptics.warmUp()
        AppNotifications.setupNotificationDelegate()
        rescheduleEngagementRemindersIfEnabled()
    }
) {
    MainAppView()
}

private fun rescheduleEngagementRemindersIfEnabled() {
    val settingsRepository: SettingsRepository = KoinPlatform.getKoin().get()
    if (settingsRepository.areEngagementRemindersEnabled()) {
        AppNotifications.scheduleEngagementReminders()
    }
}

private fun initSentry() {
    val dsn = BuildKonfig.SENTRY_DSN
    if (dsn.isNotEmpty()) {
        Sentry.init { options ->
            options.dsn = dsn
            options.environment = if (PlatformUtils.isDebugBuild) "debug" else "release"
            options.debug = PlatformUtils.isDebugBuild
        }
    }
}
