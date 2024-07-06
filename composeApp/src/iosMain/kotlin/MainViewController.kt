
import androidx.compose.ui.window.ComposeUIViewController
import core.di.KoinInitializer
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics
import dev.gitlive.firebase.initialize

fun MainViewController() = ComposeUIViewController(
    configure = {
        KoinInitializer().init()
        Firebase.initialize()
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(true)
    },
) {
    MainAppView()
}
