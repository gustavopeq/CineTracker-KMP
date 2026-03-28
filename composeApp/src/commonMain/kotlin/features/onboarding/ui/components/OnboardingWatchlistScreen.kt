package features.onboarding.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.ic_watchlist
import cinetracker_kmp.composeapp.generated.resources.onboarding_build_collection
import cinetracker_kmp.composeapp.generated.resources.onboarding_create_new_list
import cinetracker_kmp.composeapp.generated.resources.onboarding_lists_desc
import cinetracker_kmp.composeapp.generated.resources.onboarding_lists_title
import cinetracker_kmp.composeapp.generated.resources.onboarding_watchlist_1
import cinetracker_kmp.composeapp.generated.resources.onboarding_watchlist_2
import cinetracker_kmp.composeapp.generated.resources.onboarding_watchlist_3
import common.ui.theme.PrimaryBlackColor
import common.ui.theme.PrimaryYellowColor
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private const val GRID_ROTATION = -2f
private const val GRID_ALPHA = 0.8f
private const val CARD_CORNER_RADIUS = 16
private const val INNER_IMAGE_CORNER_RADIUS = 8
private const val GRID_SPACING = 16
private const val MAX_GRID_SCALE = 1.05f
private const val FADED_CARD_ALPHA = 0.4f
private const val BOOKMARK_ICON_SIZE = 32
private const val BOOKMARK_CIRCLE_SIZE = 80
private val CARD_BG_COLOR = Color(0xFF1F1F1F)
private val CARD_BORDER_COLOR = Color(0x0DFFFFFF)
private val HIGHLIGHTED_BG_COLOR = Color(0xFF262626)
private const val HIGHLIGHTED_BORDER_ALPHA = 0.4f
private const val BOOKMARK_CIRCLE_BG_ALPHA = 0.1f
private const val BOOKMARK_CIRCLE_BORDER_ALPHA = 0.2f
private val PLACEHOLDER_BAR_COLOR_1 = Color(0x1AFFFFFF)
private val PLACEHOLDER_BAR_COLOR_2 = Color(0x0DFFFFFF)

@Composable
fun OnboardingWatchlistScreen(modifier: Modifier = Modifier) {
    val saturationMatrix = ColorMatrix().apply { setToSaturation(0f) }

    Box(modifier = modifier.fillMaxSize().background(PrimaryBlackColor)) {
        // Card grid illustration
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(0.55f)
                .alpha(GRID_ALPHA),
            contentAlignment = Alignment.Center
        ) {
            val gridScale = (OnboardingConstants.GRID_REFERENCE_WIDTH / maxWidth.value * MAX_GRID_SCALE)
                .coerceIn(OnboardingConstants.MIN_GRID_SCALE, MAX_GRID_SCALE)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .rotate(GRID_ROTATION)
                    .scale(gridScale)
                    .padding(horizontal = 24.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(GRID_SPACING.dp)) {
                    // Row 1
                    Row(horizontalArrangement = Arrangement.spacedBy(GRID_SPACING.dp)) {
                        FadedCollectionCard(
                            imageRes = Res.drawable.onboarding_watchlist_1,
                            saturationMatrix = saturationMatrix,
                            modifier = Modifier.weight(1f)
                        )
                        HighlightedCreateCard(modifier = Modifier.weight(1f))
                    }
                    // Row 2
                    Row(horizontalArrangement = Arrangement.spacedBy(GRID_SPACING.dp)) {
                        FadedCollectionCard(
                            imageRes = Res.drawable.onboarding_watchlist_2,
                            saturationMatrix = saturationMatrix,
                            modifier = Modifier.weight(1f)
                        )
                        FadedCollectionCard(
                            imageRes = Res.drawable.onboarding_watchlist_3,
                            saturationMatrix = saturationMatrix,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Amber glow at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(0.5f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            PrimaryYellowColor.copy(alpha = 0.1f),
                            PrimaryYellowColor.copy(alpha = 0.05f)
                        )
                    )
                )
        )

        // Bottom gradient for text readability
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(0.55f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            PrimaryBlackColor.copy(alpha = 0.8f),
                            PrimaryBlackColor
                        )
                    )
                )
        )

        // Text content at bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = OnboardingConstants.SCREEN_HORIZONTAL_PADDING.dp)
                .padding(bottom = OnboardingConstants.TEXT_BOTTOM_PADDING.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Res.string.onboarding_lists_title),
                fontSize = OnboardingConstants.TITLE_FONT_SIZE.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = OnboardingConstants.TITLE_LINE_HEIGHT.sp,
                letterSpacing = OnboardingConstants.TITLE_LETTER_SPACING.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(OnboardingConstants.TITLE_DESC_SPACING.dp))

            Text(
                text = stringResource(Res.string.onboarding_lists_desc),
                fontSize = OnboardingConstants.DESC_FONT_SIZE.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = OnboardingConstants.DESC_LINE_HEIGHT.sp,
                color = OnboardingConstants.DESCRIPTION_COLOR,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun FadedCollectionCard(
    imageRes: DrawableResource,
    saturationMatrix: ColorMatrix,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(CARD_CORNER_RADIUS.dp)
    val innerShape = RoundedCornerShape(INNER_IMAGE_CORNER_RADIUS.dp)

    Box(
        modifier = modifier
            .aspectRatio(4f / 5f)
            .alpha(FADED_CARD_ALPHA)
            .clip(shape)
            .border(width = 1.dp, color = CARD_BORDER_COLOR, shape = shape)
            .background(CARD_BG_COLOR)
            .padding(17.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(innerShape)
                    .background(HIGHLIGHTED_BG_COLOR)
            ) {
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.colorMatrix(saturationMatrix),
                    modifier = Modifier.fillMaxSize().alpha(0.5f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Placeholder text bars
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(PLACEHOLDER_BAR_COLOR_1)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(PLACEHOLDER_BAR_COLOR_2)
            )
        }
    }
}

@Composable
private fun HighlightedCreateCard(modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(CARD_CORNER_RADIUS.dp)

    Box(
        modifier = modifier
            .aspectRatio(4f / 5f)
            .clip(shape)
            .border(width = 2.dp, color = PrimaryYellowColor.copy(alpha = HIGHLIGHTED_BORDER_ALPHA), shape = shape)
            .background(HIGHLIGHTED_BG_COLOR),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Bookmark icon in circle
            Box(
                modifier = Modifier
                    .size(BOOKMARK_CIRCLE_SIZE.dp)
                    .clip(CircleShape)
                    .border(
                        width = 1.dp,
                        color = PrimaryYellowColor.copy(alpha = BOOKMARK_CIRCLE_BORDER_ALPHA),
                        shape = CircleShape
                    )
                    .background(PrimaryYellowColor.copy(alpha = BOOKMARK_CIRCLE_BG_ALPHA)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_watchlist),
                    contentDescription = null,
                    tint = PrimaryYellowColor,
                    modifier = Modifier.size(BOOKMARK_ICON_SIZE.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(Res.string.onboarding_create_new_list),
                fontSize = OnboardingConstants.CARD_LABEL_FONT_SIZE.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 28.sp,
                color = PrimaryYellowColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(Res.string.onboarding_build_collection),
                fontSize = OnboardingConstants.CARD_SUBLABEL_FONT_SIZE.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 16.sp,
                color = OnboardingConstants.DESCRIPTION_COLOR,
                textAlign = TextAlign.Center
            )
        }
    }
}
