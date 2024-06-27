package network.di

import network.repository.home.HomeRepository
import network.repository.home.HomeRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<HomeRepository> { HomeRepositoryImpl(get()) }
}
