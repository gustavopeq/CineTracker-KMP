package auth.di

import auth.network.supabaseClient
import auth.repository.AuthRepository
import auth.repository.AuthRepositoryImpl
import auth.service.SupabaseAuthService
import auth.service.SupabaseAuthServiceImpl
import auth.service.SyncService
import auth.service.SyncServiceImpl
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

expect fun platformAuthModule(): Module

val authModule = module {
    single<SupabaseAuthService> { SupabaseAuthServiceImpl(supabaseClient) }
    single<SyncService> {
        SyncServiceImpl(get(), get(), get(), get(), get(), get(), get(named("appScope")))
    }
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get(), get(), get()) }
}
