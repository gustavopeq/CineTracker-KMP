package core.di

import android.content.Context
import core.di.modules.interactorModule
import core.di.modules.viewModelModule
import database.di.daoModule
import database.di.databaseModule
import database.di.databaseRepositoryModule
import network.di.apiModule
import network.di.repositoryModule
import network.di.serviceModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

actual class KoinInitializer(
    private val context: Context,
) {
    actual fun init() {
        startKoin {
            androidContext(context)
            modules(
                interactorModule,
                viewModelModule,
                databaseModule(),
                daoModule,
                databaseRepositoryModule,
                apiModule,
                serviceModule,
                repositoryModule,
            )
        }
    }
}
