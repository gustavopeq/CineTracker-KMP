package auth.di

import auth.platform.PlatformSignInProvider
import auth.platform.TokenStorage
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformAuthModule(): Module = module {
    single { TokenStorage(get()) }
    single { PlatformSignInProvider(get()) }
}
