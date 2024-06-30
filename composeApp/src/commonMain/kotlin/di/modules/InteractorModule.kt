package di.modules

import features.details.domain.DetailsInteractor
import features.home.ui.domain.HomeInteractor
import org.koin.dsl.module

val interactorModule = module {
    single<DetailsInteractor> { DetailsInteractor(get(), get(), get()) }
    single { HomeInteractor(get()) }
}
