package core.di

import android.content.Context
import core.di.modules.interactorModule
import core.di.modules.viewModelModule
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
                apiModule,
                serviceModule,
                repositoryModule,
            )
        }
    }
}
