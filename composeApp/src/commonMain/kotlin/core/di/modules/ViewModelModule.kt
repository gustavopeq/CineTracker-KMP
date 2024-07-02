package core.di.modules

import common.domain.models.util.MediaType
import common.ui.MainViewModel
import features.browse.ui.BrowseViewModel
import features.details.ui.DetailsViewModel
import features.home.ui.HomeViewModel
import features.search.ui.SearchViewModel
import features.watchlist.ui.WatchlistViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    single { MainViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { BrowseViewModel(get()) }
    viewModel { WatchlistViewModel(get()) }
    viewModel { SearchViewModel(get()) }
    viewModel {
            (contentId: Int, mediaType: MediaType) ->
        DetailsViewModel(contentId, mediaType, get())
    }
}
