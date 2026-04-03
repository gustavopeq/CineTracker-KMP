package common.util.platform

import androidx.compose.runtime.Composable

@Composable
expect fun rememberNotificationPermissionLauncher(onResult: (Boolean) -> Unit): () -> Unit
