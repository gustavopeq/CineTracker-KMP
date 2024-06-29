package common.ui.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun ClassicLoadingIndicator() {
    CircularProgressIndicator(
        color = MaterialTheme.colorScheme.secondary,
    )
}
