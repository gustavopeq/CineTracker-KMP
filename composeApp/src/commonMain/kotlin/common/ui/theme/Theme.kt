package common.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorPalette =
    darkColorScheme(
        primary = PrimaryBlackColor,
        onPrimary = PrimaryWhiteColor,
        secondary = PrimaryYellowColor,
        onSecondary = PrimaryBlackColor,
        tertiary = PrimaryGreyColor,
        surface = SecondaryGreyColor,
        onSurface = PrimaryWhiteColor,
        onSurfaceVariant = PrimaryYellowColor,
        inverseSurface = DividerGrey,
        surfaceVariant = PrimaryGreyColor_55,
        background = PrimaryBlackColor,
        error = PrimaryRedColor,
    )

@Composable
fun CineTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content:
        @Composable()
        () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorPalette,
        typography = AppTypography,
        shapes = RoundCornerShapes,
        content = content,
    )
}
