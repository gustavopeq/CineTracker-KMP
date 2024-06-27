package di.modules

import features.home.ui.domain.HomeInteractor
import org.koin.dsl.module

val interactorModule = module {
    single { HomeInteractor(get()) }
}
