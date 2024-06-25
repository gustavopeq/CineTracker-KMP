package features.home.ui

import androidx.lifecycle.ViewModel
import features.home.ui.domain.HomeInteractor

class HomeViewModel(
    private val homeInteractor: HomeInteractor,
) : ViewModel()
