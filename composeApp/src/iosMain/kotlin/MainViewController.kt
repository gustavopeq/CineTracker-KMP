import androidx.compose.ui.window.ComposeUIViewController
import core.di.KoinInitializer

fun MainViewController() = ComposeUIViewController(
    configure = {
        KoinInitializer().init()
    },
) {
    MainAppView()
}
