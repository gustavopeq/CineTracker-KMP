package di.modules

import common.domain.models.util.MediaType
import features.details.ui.DetailsViewModel
import features.home.ui.HomeViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel {
            (contentId: Int, mediaType: MediaType) ->
        DetailsViewModel(contentId, mediaType, get())
    }
}
