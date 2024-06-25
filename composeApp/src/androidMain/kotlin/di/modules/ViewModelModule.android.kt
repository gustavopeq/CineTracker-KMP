package di.modules
import features.home.ui.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

actual val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
}
