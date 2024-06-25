package di.modules

import features.home.ui.HomeViewModel
import org.koin.dsl.module

actual val viewModelModule = module {
    single { HomeViewModel(get()) }
}
