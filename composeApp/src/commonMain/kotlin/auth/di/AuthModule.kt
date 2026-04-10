package auth.di

import auth.network.supabaseClient
import auth.repository.AuthRepository
import auth.repository.AuthRepositoryImpl
import auth.service.SupabaseAuthService
import auth.service.SupabaseAuthServiceImpl
import org.koin.core.module.Module
import org.koin.dsl.module

expect fun platformAuthModule(): Module

val authModule = module {
    single<SupabaseAuthService> { SupabaseAuthServiceImpl(supabaseClient) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get(), get()) }
}
