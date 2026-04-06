package core.di.modules

import database.backfill.CachedFieldsBackfill
import features.browse.domain.BrowseInteractor
import features.details.domain.DetailsInteractor
import features.home.domain.HomeInteractor
import features.search.domain.SearchInteractor
import features.settings.domain.SettingsInteractor
import features.watchlist.domain.ListInteractor
import features.watchlist.domain.WatchlistInteractor
import org.koin.dsl.module

val interactorModule = module {
    single<BrowseInteractor> { BrowseInteractor(get(), get(), get()) }
    single<DetailsInteractor> { DetailsInteractor(get(), get(), get(), get(), get(), get(), get()) }
    single<WatchlistInteractor> { WatchlistInteractor(get(), get()) }
    single<SearchInteractor> { SearchInteractor(get(), get()) }
    single { HomeInteractor(get(), get(), get()) }
    single { ListInteractor(get()) }
    single { CachedFieldsBackfill(get(), get(), get(), get()) }
    single { SettingsInteractor(get()) }
}
