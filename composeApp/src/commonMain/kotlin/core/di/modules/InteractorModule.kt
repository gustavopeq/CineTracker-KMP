package core.di.modules

import features.browse.domain.BrowseInteractor
import features.details.domain.DetailsInteractor
import features.home.domain.HomeInteractor
import features.search.domain.SearchInteractor
import features.watchlist.domain.WatchlistInteractor
import org.koin.dsl.module

val interactorModule = module {
    single<BrowseInteractor> { BrowseInteractor(get(), get()) }
    single<DetailsInteractor> { DetailsInteractor(get(), get(), get(), get()) }
    single<WatchlistInteractor> { WatchlistInteractor(get(), get(), get()) }
    single<SearchInteractor> { SearchInteractor(get()) }
    single { HomeInteractor(get(), get(), get(), get()) }
}
