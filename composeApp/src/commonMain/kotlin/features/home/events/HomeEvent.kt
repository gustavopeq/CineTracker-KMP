package features.home.events

sealed class HomeEvent {
    data object LoadHome : HomeEvent()
    data object OnError : HomeEvent()
    data class ToggleFeaturedFromList(val listId: Int) : HomeEvent()
    data object OpenListBottomSheet : HomeEvent()
    data object CloseListBottomSheet : HomeEvent()
}
