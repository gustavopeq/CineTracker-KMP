package common.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val AppTypography =
    Typography(
        // Display
        displayLarge =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 32.sp,
                fontWeight = FontWeight(400),
                lineHeight = 28.sp,
            ),
        displayMedium =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 28.sp,
                fontWeight = FontWeight(400),
                lineHeight = 28.sp,
            ),
        displaySmall =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 23.sp,
                fontWeight = FontWeight(400),
                lineHeight = 28.sp,
            ),
        // Headline
        headlineLarge =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 20.sp,
                fontWeight = FontWeight(420),
                lineHeight = 28.sp,
            ),
        headlineMedium =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 18.sp,
                fontWeight = FontWeight(400),
                lineHeight = 28.sp,
            ),
        headlineSmall =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 16.sp,
                fontWeight = FontWeight(400),
                lineHeight = 28.sp,
            ),
        // Title
        titleLarge =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 22.sp,
                fontWeight = FontWeight(400),
                lineHeight = 28.sp,
            ),
        titleMedium =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 18.sp,
                fontWeight = FontWeight(500),
                lineHeight = 24.sp,
            ),
        titleSmall =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 14.sp,
                fontWeight = FontWeight(500),
                lineHeight = 24.sp,
            ),
        // Body
        bodyLarge =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 24.sp,
                fontWeight = FontWeight(400),
                lineHeight = 24.sp,
            ),
        bodyMedium =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 16.sp,
                fontWeight = FontWeight(400),
                lineHeight = 20.sp,
            ),
        bodySmall =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 14.sp,
                fontWeight = FontWeight(400),
                lineHeight = 20.sp,
            ),
        // Label
        labelMedium =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 18.sp,
                fontWeight = FontWeight(400),
                lineHeight = 24.sp,
            ),
        labelSmall =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 12.sp,
                fontWeight = FontWeight(400),
                lineHeight = 20.sp,
            ),
    )
