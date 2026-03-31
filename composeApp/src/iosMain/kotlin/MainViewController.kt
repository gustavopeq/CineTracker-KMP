
import androidx.compose.ui.window.ComposeUIViewController
import com.projects.cinetracker.BuildKonfig
import common.util.platform.AppHaptics
import common.util.platform.PlatformUtils
import core.di.KoinInitializer
import io.sentry.kotlin.multiplatform.Sentry

fun MainViewController() = ComposeUIViewController(
    configure = {
        KoinInitializer().init()
        initSentry()
        AppHaptics.warmUp()
    }
) {
    MainAppView()
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
