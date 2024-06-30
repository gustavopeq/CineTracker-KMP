package network.di

import network.repository.home.HomeRepository
import network.repository.home.HomeRepositoryImpl
import network.repository.movie.MovieRepository
import network.repository.movie.MovieRepositoryImpl
import network.repository.person.PersonRepository
import network.repository.person.PersonRepositoryImpl
import network.repository.show.ShowRepository
import network.repository.show.ShowRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<MovieRepository> { MovieRepositoryImpl(get()) }
    single<ShowRepository> { ShowRepositoryImpl(get()) }
    single<PersonRepository> { PersonRepositoryImpl(get()) }
    single<HomeRepository> { HomeRepositoryImpl(get()) }
}
