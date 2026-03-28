package core.di.modules

import common.domain.models.util.MediaType
import common.ui.MainViewModel
import features.browse.ui.BrowseViewModel
import features.details.ui.DetailsViewModel
import features.home.ui.HomeViewModel
import features.onboarding.ui.OnboardingViewModel
import features.search.ui.SearchViewModel
import features.watchlist.ui.WatchlistViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    single { MainViewModel(get(), get()) }
    viewModel { OnboardingViewModel(get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { BrowseViewModel(get()) }
    viewModel { WatchlistViewModel(get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { (contentId: Int, mediaType: MediaType) ->
        DetailsViewModel(contentId, mediaType, get())
    }
}
