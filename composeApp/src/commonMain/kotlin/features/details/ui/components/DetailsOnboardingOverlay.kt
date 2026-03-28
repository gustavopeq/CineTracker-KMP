package features.details.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.details_overlay_cta
import cinetracker_kmp.composeapp.generated.resources.details_overlay_subtitle
import cinetracker_kmp.composeapp.generated.resources.details_overlay_title_movie
import cinetracker_kmp.composeapp.generated.resources.details_overlay_title_show
import cinetracker_kmp.composeapp.generated.resources.ic_watchlist
import common.domain.models.util.MediaType
import common.ui.components.button.GenericButton
import common.ui.theme.PrimaryYellowColor
import common.util.UiConstants.DEFAULT_MARGIN
import common.util.UiConstants.DETAILS_OVERLAY_ICON_SIZE
import common.util.UiConstants.OVERLAY_INDEX
import common.util.UiConstants.SMALL_PADDING
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private const val OVERLAY_BACKGROUND_ALPHA = 0.85f
private const val GLOW_BLUR = 24
private const val GLOW_ALPHA = 0.5f
private const val CONTENT_FADE_DURATION_MS = 200
private const val BACKGROUND_FADE_DURATION_MS = 250
private const val BACKGROUND_FADE_DELAY_MS = 50L
private const val ICON_FLY_DURATION_MS = 700
private const val ICON_FINAL_FADE_DURATION_MS = 100
private const val OVERLAY_TEXT_FONT_SIZE = 26
private const val ICON_TEXT_SPACING = 12
private const val TEXT_CTA_SPACING = 32
private const val TARGET_ICON_SIZE = 24

@Composable
fun DetailsOnboardingOverlay(
    targetIconPosition: Offset,
    mediaType: MediaType,
    onDismissStarted: () -> Unit,
    onDismiss: () -> Unit
) {
    val density = LocalDensity.current
    val overlayIconSizePx = with(density) { DETAILS_OVERLAY_ICON_SIZE.dp.toPx() }
    val targetIconSizePx = with(density) { TARGET_ICON_SIZE.dp.toPx() }

    val backgroundAlpha = remember { Animatable(OVERLAY_BACKGROUND_ALPHA) }
    val contentAlpha = remember { Animatable(1f) }
    val iconProgress = remember { Animatable(0f) }
    val flyingIconAlpha = remember { Animatable(0f) }

    var isDismissing by remember { mutableStateOf(false) }
    var overlayWindowPosition by remember { mutableStateOf(Offset.Zero) }
    var centerIconPosition by remember { mutableStateOf(Offset.Zero) }

    val titleRes = when (mediaType) {
        MediaType.MOVIE -> Res.string.details_overlay_title_movie
        else -> Res.string.details_overlay_title_show
    }

    // Convert target position from window coordinates to overlay-relative coordinates
    val relativeTargetPosition = if (targetIconPosition != Offset.Zero && overlayWindowPosition != Offset.Zero) {
        Offset(
            x = targetIconPosition.x - overlayWindowPosition.x,
            y = targetIconPosition.y - overlayWindowPosition.y
        )
    } else {
        Offset.Zero
    }

    LaunchedEffect(isDismissing) {
        if (isDismissing && centerIconPosition != Offset.Zero && relativeTargetPosition != Offset.Zero) {
            onDismissStarted()
            flyingIconAlpha.snapTo(1f)

            launch { contentAlpha.animateTo(0f, tween(CONTENT_FADE_DURATION_MS)) }

            launch {
                delay(BACKGROUND_FADE_DELAY_MS)
                backgroundAlpha.animateTo(0f, tween(BACKGROUND_FADE_DURATION_MS))
            }

            launch {
                iconProgress.animateTo(
                    1f,
                    tween(ICON_FLY_DURATION_MS, easing = FastOutSlowInEasing)
                )
                onDismiss()
            }
        }
    }

    val progress = iconProgress.value

    val currentIconSizePx = overlayIconSizePx + (targetIconSizePx - overlayIconSizePx) * progress
    val currentIconSize = with(density) { currentIconSizePx.toDp() }

    val currentOffset = if (centerIconPosition != Offset.Zero && relativeTargetPosition != Offset.Zero) {
        val startX = centerIconPosition.x - overlayIconSizePx / 2f
        val startY = centerIconPosition.y - overlayIconSizePx / 2f
        val endX = relativeTargetPosition.x - currentIconSizePx / 2f
        val endY = relativeTargetPosition.y - currentIconSizePx / 2f
        IntOffset(
            x = (startX + (endX - startX) * progress).toInt(),
            y = (startY + (endY - startY) * progress).toInt()
        )
    } else {
        IntOffset.Zero
    }

    val currentIconColor = lerp(
        PrimaryYellowColor,
        Color.White,
        iconProgress.value
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(OVERLAY_INDEX)
            .onGloballyPositioned { coordinates ->
                overlayWindowPosition = coordinates.positionInWindow()
            }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent()
                    }
                }
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = backgroundAlpha.value))
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(contentAlpha.value)
                .padding(horizontal = DEFAULT_MARGIN.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(DETAILS_OVERLAY_ICON_SIZE.dp)
                    .onGloballyPositioned { coordinates ->
                        val position = coordinates.positionInWindow()
                        val size = coordinates.size
                        centerIconPosition = Offset(
                            x = position.x - overlayWindowPosition.x + size.width / 2f,
                            y = position.y - overlayWindowPosition.y + size.height / 2f
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(resource = Res.drawable.ic_watchlist),
                    contentDescription = null,
                    modifier = Modifier
                        .matchParentSize()
                        .blur(GLOW_BLUR.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                        .alpha(GLOW_ALPHA),
                    tint = PrimaryYellowColor
                )
                Icon(
                    painter = painterResource(resource = Res.drawable.ic_watchlist),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    tint = PrimaryYellowColor
                )
            }

            Spacer(modifier = Modifier.height(ICON_TEXT_SPACING.dp))

            Text(
                text = stringResource(resource = titleRes),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = OVERLAY_TEXT_FONT_SIZE.sp

                ),
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(SMALL_PADDING.dp))

            Text(
                text = stringResource(resource = Res.string.details_overlay_subtitle),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = OVERLAY_TEXT_FONT_SIZE.sp

                ),
                color = PrimaryYellowColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(TEXT_CTA_SPACING.dp))

            GenericButton(
                buttonText = stringResource(resource = Res.string.details_overlay_cta),
                onClick = { isDismissing = true }
            )
        }

        if (isDismissing) {
            Icon(
                painter = painterResource(resource = Res.drawable.ic_watchlist),
                contentDescription = null,
                modifier = Modifier
                    .size(currentIconSize)
                    .offset { currentOffset }
                    .alpha(flyingIconAlpha.value),
                tint = currentIconColor
            )
        }
    }
}
