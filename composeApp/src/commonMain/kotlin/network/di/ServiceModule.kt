package network.di

import network.services.home.HomeService
import network.services.home.HomeServiceImpl
import org.koin.dsl.module

val serviceModule = module {
    single<HomeService> { HomeServiceImpl(get()) }
}
