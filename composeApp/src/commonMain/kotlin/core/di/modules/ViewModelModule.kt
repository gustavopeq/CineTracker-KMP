package core.di.modules

import common.domain.models.util.MediaType
import common.ui.MainViewModel
import features.auth.ui.AuthViewModel
import features.browse.ui.BrowseViewModel
import features.details.ui.DetailsViewModel
import features.home.ui.HomeViewModel
import features.onboarding.ui.OnboardingViewModel
import features.search.ui.SearchViewModel
import features.settings.ui.SettingsViewModel
import features.watchlist.ui.WatchlistViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    single { MainViewModel(get(), get(), get()) }
    viewModel { OnboardingViewModel(get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { BrowseViewModel(get()) }
    viewModel { WatchlistViewModel(get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { SettingsViewModel(get(), get()) }
    viewModel { AuthViewModel(get()) }
    viewModel { (contentId: Int, mediaType: MediaType) ->
        DetailsViewModel(contentId, mediaType, get(), get())
    }
}
