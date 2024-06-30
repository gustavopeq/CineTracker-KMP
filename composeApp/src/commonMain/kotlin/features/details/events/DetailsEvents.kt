package features.details.events

sealed class DetailsEvents {
    data object FetchDetails : DetailsEvents()
    data object OnError : DetailsEvents()
    data object OnSnackbarDismiss : DetailsEvents()
    data class ToggleContentFromList(
        val listId: Int,
    ) : DetailsEvents()
}
