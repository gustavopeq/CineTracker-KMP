package common.domain.models.util

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

open class SnackbarState {
    var displaySnackbar: MutableState<Boolean> = mutableStateOf(false)

    fun setSnackbarVisible() {
        displaySnackbar.value = true
    }

    fun setSnackbarGone() {
        displaySnackbar.value = false
    }
}
