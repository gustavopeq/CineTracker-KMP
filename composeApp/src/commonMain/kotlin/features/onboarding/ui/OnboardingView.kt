package features.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.onboarding_next
import common.ui.theme.OnboardingButtonTextColor
import common.ui.theme.PrimaryBlackColor
import common.ui.theme.PrimaryYellowColor
import features.onboarding.ui.components.OnboardingBrowseScreen
import features.onboarding.ui.components.OnboardingConstants.CONTROLS_BOTTOM_PADDING
import features.onboarding.ui.components.OnboardingConstants.CONTROLS_HORIZONTAL_PADDING
import features.onboarding.ui.components.OnboardingConstants.CONTROLS_SPACING
import features.onboarding.ui.components.OnboardingIndicator
import features.onboarding.ui.components.OnboardingWatchlistScreen
import features.onboarding.ui.components.OnboardingWelcomeScreen
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private const val PAGE_COUNT = 3
private const val BUTTON_HEIGHT = 56
private const val BUTTON_CORNER_RADIUS = 12
private const val BUTTON_FONT_SIZE = 18
private val BUTTON_TEXT_COLOR = OnboardingButtonTextColor

@Composable
fun OnboardingView(onOnboardingComplete: () -> Unit) {
    val viewModel: OnboardingViewModel = koinViewModel()
    val pagerState = rememberPagerState(pageCount = { PAGE_COUNT })
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBlackColor)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        // Pager content
        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = PAGE_COUNT - 1,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> OnboardingWelcomeScreen()
                1 -> OnboardingBrowseScreen()
                2 -> OnboardingWatchlistScreen()
            }
        }

        // Bottom controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = CONTROLS_HORIZONTAL_PADDING.dp)
                .padding(bottom = CONTROLS_BOTTOM_PADDING.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(CONTROLS_SPACING.dp)
        ) {
            OnboardingIndicator(
                pageCount = PAGE_COUNT,
                currentPage = pagerState.currentPage
            )

            OnboardingButton(
                text = stringResource(Res.string.onboarding_next),
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            )
        }
    }
}

@Composable
private fun OnboardingButton(text: String, onClick: () -> Unit) {
    val shape = RoundedCornerShape(BUTTON_CORNER_RADIUS.dp)

    Button(
        onClick = onClick,
        shape = shape,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .height(BUTTON_HEIGHT.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(PrimaryYellowColor, PrimaryYellowColor.copy(alpha = 0.85f))
                ),
                shape = shape
            )
    ) {
        Text(
            text = text,
            fontSize = BUTTON_FONT_SIZE.sp,
            fontWeight = FontWeight.Bold,
            color = BUTTON_TEXT_COLOR
        )
    }
}
