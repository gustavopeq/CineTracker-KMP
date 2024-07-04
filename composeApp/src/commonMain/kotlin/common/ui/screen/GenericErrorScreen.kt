package common.ui.screen

import KottieAnimation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.generic_error_message
import cinetracker_kmp.composeapp.generated.resources.try_again_button
import common.domain.util.UiConstants.DEFAULT_PADDING
import common.domain.util.UiConstants.ERROR_ANIMATION_SIZE
import common.domain.util.UiConstants.SECTION_PADDING
import common.ui.components.button.GenericButton
import kottieComposition.KottieCompositionSpec
import kottieComposition.animateKottieCompositionAsState
import kottieComposition.rememberKottieComposition
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@Composable
fun ErrorScreen(
    onTryAgain: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(0.35f))
        ErrorIconAnimation()
        Spacer(modifier = Modifier.height(DEFAULT_PADDING.dp))
        Text(
            text = stringResource(resource = Res.string.generic_error_message),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onPrimary,
        )
        Spacer(modifier = Modifier.height(SECTION_PADDING.dp))

        GenericButton(
            buttonText = stringResource(resource = Res.string.try_again_button),
            onClick = onTryAgain,
        )
        Spacer(modifier = Modifier.weight(0.65f))
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ErrorIconAnimation() {
    var animation by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        animation = Res.readBytes("files/erroranimation.json").decodeToString()
    }

    val composition = rememberKottieComposition(
        spec = KottieCompositionSpec.File(animation),
    )

    val animationState by animateKottieCompositionAsState(
        composition = composition,
        iterations = 2,
    )

    KottieAnimation(
        modifier = Modifier.size(ERROR_ANIMATION_SIZE.dp),
        composition = composition,
        progress = { animationState.progress },
        backgroundColor = MaterialTheme.colorScheme.primary,
    )
}
