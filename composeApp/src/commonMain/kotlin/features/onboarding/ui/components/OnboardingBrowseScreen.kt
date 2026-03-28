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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.ic_nav_search
import cinetracker_kmp.composeapp.generated.resources.onboarding_discover_desc
import cinetracker_kmp.composeapp.generated.resources.onboarding_discover_title
import cinetracker_kmp.composeapp.generated.resources.onboarding_browse_1
import cinetracker_kmp.composeapp.generated.resources.onboarding_browse_2
import cinetracker_kmp.composeapp.generated.resources.onboarding_browse_3
import cinetracker_kmp.composeapp.generated.resources.onboarding_browse_4
import cinetracker_kmp.composeapp.generated.resources.onboarding_browse_5
import common.ui.theme.PrimaryBlackColor
import common.ui.theme.PrimaryYellowColor
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private const val GRID_ROTATION = 6f
private const val GRID_ALPHA = 0.5f
private const val CARD_CORNER_RADIUS = 8
private const val SEARCH_ICON_SIZE = 27
private const val GRID_SPACING = 12
private const val MAX_GRID_SCALE = 1.25f
private val CARD_BG_COLOR = Color(0xFF191919)
private val CARD_BORDER_COLOR = Color(0x0DFFFFFF)
private val HIGHLIGHTED_BORDER_COLOR = Color(0x1AFFFFFF)
private const val HIGHLIGHTED_GLOW_ALPHA = 0.2f
private val SEARCH_BG_COLOR = Color(0xFF1F1F1F)

@Composable
fun OnboardingBrowseScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().background(PrimaryBlackColor)) {
        // Poster grid illustration
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
                    .padding(horizontal = 16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(GRID_SPACING.dp)) {
                    // Row 1
                    Row(horizontalArrangement = Arrangement.spacedBy(GRID_SPACING.dp)) {
                        PosterCard(
                            imageRes = Res.drawable.onboarding_browse_1,
                            modifier = Modifier.weight(1f)
                        )
                        PosterCard(
                            imageRes = Res.drawable.onboarding_browse_2,
                            modifier = Modifier.weight(1f)
                        )
                        PosterCard(
                            imageRes = Res.drawable.onboarding_browse_3,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Row 2
                    Row(horizontalArrangement = Arrangement.spacedBy(GRID_SPACING.dp)) {
                        PosterCard(
                            imageRes = Res.drawable.onboarding_browse_4,
                            modifier = Modifier.weight(1f)
                        )
                        PosterCard(
                            imageRes = Res.drawable.onboarding_browse_5,
                            modifier = Modifier.weight(1f),
                            isHighlighted = true
                        )
                        SearchPlaceholderCard(modifier = Modifier.weight(1f))
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
                .padding(horizontal = 32.dp)
                .padding(bottom = 180.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Res.string.onboarding_discover_title),
                fontSize = OnboardingConstants.TITLE_FONT_SIZE.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = OnboardingConstants.TITLE_LINE_HEIGHT.sp,
                letterSpacing = OnboardingConstants.TITLE_LETTER_SPACING.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(Res.string.onboarding_discover_desc),
                fontSize = OnboardingConstants.DESC_FONT_SIZE.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = OnboardingConstants.DESC_LINE_HEIGHT.sp,
                color = OnboardingConstants.DESCRIPTION_COLOR,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun PosterCard(imageRes: DrawableResource, modifier: Modifier = Modifier, isHighlighted: Boolean = false) {
    val shape = RoundedCornerShape(CARD_CORNER_RADIUS.dp)
    val borderModifier = if (isHighlighted) {
        modifier
            .aspectRatio(2f / 3f)
            .clip(shape)
            .border(width = 1.dp, color = HIGHLIGHTED_BORDER_COLOR, shape = shape)
            .background(
                Brush.radialGradient(
                    colors = listOf(PrimaryYellowColor.copy(alpha = HIGHLIGHTED_GLOW_ALPHA), Color.Transparent)
                )
            )
    } else {
        modifier
            .aspectRatio(2f / 3f)
            .clip(shape)
            .border(width = 1.dp, color = CARD_BORDER_COLOR, shape = shape)
            .background(CARD_BG_COLOR)
    }

    Box(modifier = borderModifier) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun SearchPlaceholderCard(modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(CARD_CORNER_RADIUS.dp)

    Box(
        modifier = modifier
            .aspectRatio(2f / 3f)
            .clip(shape)
            .border(width = 1.dp, color = CARD_BORDER_COLOR, shape = shape)
            .background(SEARCH_BG_COLOR),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_nav_search),
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.3f),
            modifier = Modifier.size(SEARCH_ICON_SIZE.dp)
        )
    }
}
