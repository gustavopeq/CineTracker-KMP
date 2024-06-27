package di

import di.modules.interactorModule
import di.modules.viewModelModule
import network.di.apiModule
import network.di.repositoryModule
import network.di.serviceModule
import org.koin.core.context.startKoin

actual class KoinInitializer {
    actual fun init() {
        startKoin {
            modules(
                interactorModule,
                viewModelModule,
                apiModule,
                serviceModule,
                repositoryModule,
            )
        }
    }
}
