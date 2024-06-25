package di

import di.modules.interactorModule
import di.modules.viewModelModule
import org.koin.core.context.startKoin

actual class KoinInitializer {
    actual fun init() {
        startKoin {
            modules(
                interactorModule,
                viewModelModule,
            )
        }
    }
}
