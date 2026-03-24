package features.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.onboarding_discover_desc
import cinetracker_kmp.composeapp.generated.resources.onboarding_discover_title
import cinetracker_kmp.composeapp.generated.resources.onboarding_get_started
import cinetracker_kmp.composeapp.generated.resources.onboarding_lists_desc
import cinetracker_kmp.composeapp.generated.resources.onboarding_lists_title
import cinetracker_kmp.composeapp.generated.resources.onboarding_next
import cinetracker_kmp.composeapp.generated.resources.onboarding_skip
import cinetracker_kmp.composeapp.generated.resources.onboarding_welcome_desc
import cinetracker_kmp.composeapp.generated.resources.onboarding_welcome_title
import common.ui.components.button.GenericButton
import common.ui.theme.PrimaryBlackColor
import common.util.UiConstants.DEFAULT_MARGIN
import common.util.UiConstants.LARGE_MARGIN
import features.onboarding.ui.components.DiscoverIllustration
import features.onboarding.ui.components.ListsIllustration
import features.onboarding.ui.components.OnboardingIndicator
import features.onboarding.ui.components.OnboardingPage
import features.onboarding.ui.components.WelcomeIllustration
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private const val PAGE_COUNT = 3

@Composable
fun OnboardingView(onOnboardingComplete: () -> Unit) {
    val viewModel: OnboardingViewModel = koinViewModel()
    val pagerState = rememberPagerState(pageCount = { PAGE_COUNT })
    val coroutineScope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == PAGE_COUNT - 1

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBlackColor)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        // Skip button (hidden on last page)
        if (!isLastPage) {
            Text(
                text = stringResource(Res.string.onboarding_skip),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(DEFAULT_MARGIN.dp)
                    .clickable {
                        viewModel.completeOnboarding(onOnboardingComplete)
                    }
            )
        }

        // Pager content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> OnboardingPage(
                    title = stringResource(Res.string.onboarding_welcome_title),
                    description = stringResource(Res.string.onboarding_welcome_desc),
                    illustration = { WelcomeIllustration() }
                )
                1 -> OnboardingPage(
                    title = stringResource(Res.string.onboarding_discover_title),
                    description = stringResource(Res.string.onboarding_discover_desc),
                    illustration = { DiscoverIllustration() }
                )
                2 -> OnboardingPage(
                    title = stringResource(Res.string.onboarding_lists_title),
                    description = stringResource(Res.string.onboarding_lists_desc),
                    illustration = { ListsIllustration() }
                )
            }
        }

        // Bottom controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = LARGE_MARGIN.dp, vertical = LARGE_MARGIN.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OnboardingIndicator(
                pageCount = PAGE_COUNT,
                currentPage = pagerState.currentPage
            )

            GenericButton(
                buttonText = if (isLastPage) {
                    stringResource(Res.string.onboarding_get_started)
                } else {
                    stringResource(Res.string.onboarding_next)
                },
                onClick = {
                    if (isLastPage) {
                        viewModel.completeOnboarding(onOnboardingComplete)
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                }
            )
        }
    }
}
