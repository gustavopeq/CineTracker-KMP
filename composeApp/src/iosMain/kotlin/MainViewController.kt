
import androidx.compose.ui.window.ComposeUIViewController
import common.util.platform.AppHaptics
import core.di.KoinInitializer

fun MainViewController() = ComposeUIViewController(
    configure = {
        KoinInitializer().init()
        AppHaptics.warmUp()
    }
) {
    MainAppView()
}
