package common.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.generic_error_message
import cinetracker_kmp.composeapp.generated.resources.try_again_button
import common.ui.components.button.GenericButton
import common.util.UiConstants.DEFAULT_PADDING
import common.util.UiConstants.ERROR_ANIMATION_SIZE
import common.util.UiConstants.SECTION_PADDING
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@Composable
fun ErrorScreen(onTryAgain: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.35f))
        ErrorIconAnimation()
        Spacer(modifier = Modifier.height(DEFAULT_PADDING.dp))
        Text(
            text = stringResource(resource = Res.string.generic_error_message),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(modifier = Modifier.height(SECTION_PADDING.dp))

        GenericButton(
            buttonText = stringResource(resource = Res.string.try_again_button),
            onClick = onTryAgain
        )
        Spacer(modifier = Modifier.weight(0.65f))
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ErrorIconAnimation() {
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/erroranimation.json").decodeToString()
        )
    }

    Image(
        painter = rememberLottiePainter(
            composition = composition,
            iterations = 2
        ),
        contentDescription = null,
        modifier = Modifier.size(ERROR_ANIMATION_SIZE.dp)
    )
}
