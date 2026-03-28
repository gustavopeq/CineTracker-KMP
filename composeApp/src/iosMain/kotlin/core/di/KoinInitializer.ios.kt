package core.di

import core.di.modules.interactorModule
import core.di.modules.viewModelModule
import database.di.daoModule
import database.di.databaseModule
import database.di.databaseRepositoryModule
import database.di.settingsModule
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
                databaseModule(),
                daoModule,
                databaseRepositoryModule,
                settingsModule(),
                apiModule,
                serviceModule,
                repositoryModule
            )
        }
    }
}
