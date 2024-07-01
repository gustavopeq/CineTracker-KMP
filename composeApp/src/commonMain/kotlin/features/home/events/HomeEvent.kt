package features.home.events

sealed class HomeEvent {
    data object LoadHome : HomeEvent()
    data object ReloadWatchlist : HomeEvent()
    data object OnError : HomeEvent()
}
