package network.di

import network.services.home.HomeService
import network.services.home.HomeServiceImpl
import network.services.movie.MovieService
import network.services.movie.MovieServiceImpl
import network.services.person.PersonService
import network.services.person.PersonServiceImpl
import network.services.show.ShowService
import network.services.show.ShowServiceImpl
import org.koin.dsl.module

val serviceModule = module {
    single<MovieService> { MovieServiceImpl(get()) }
    single<ShowService> { ShowServiceImpl(get()) }
    single<PersonService> { PersonServiceImpl(get()) }
    single<HomeService> { HomeServiceImpl(get()) }
}
