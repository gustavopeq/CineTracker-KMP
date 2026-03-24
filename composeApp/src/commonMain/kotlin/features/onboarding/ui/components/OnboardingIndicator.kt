package features.onboarding.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import common.ui.theme.PrimaryYellowColor
import common.ui.theme.SecondaryGreyColor
import common.util.UiConstants.DEFAULT_PADDING

private const val INDICATOR_SIZE = 8
private const val INACTIVE_INDICATOR_ALPHA = 0.4f

@Composable
fun OnboardingIndicator(pageCount: Int, currentPage: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(DEFAULT_PADDING.dp)
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(INDICATOR_SIZE.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == currentPage) {
                            PrimaryYellowColor
                        } else {
                            SecondaryGreyColor.copy(alpha = INACTIVE_INDICATOR_ALPHA)
                        }
                    )
            )
        }
    }
}
