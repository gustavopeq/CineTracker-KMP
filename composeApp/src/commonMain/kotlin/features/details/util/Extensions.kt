package features.details.util

import androidx.compose.runtime.Composable

@Composable
fun Int.formatRuntime(): String {
    val runtimeHours: Int = this / 60
    val runtimeMinutes = this % 60

    return if (runtimeHours != 0 && runtimeMinutes != 0) {
        "${runtimeHours}h ${runtimeMinutes}m"
    } else if (runtimeHours != 0) {
        "${runtimeHours}h"
    } else {
        "${runtimeMinutes}m"
    }
}

/**
 * Function to convert header position into a value between 0.0 and 1.0.
 * Used to define the background image alpha based on the header position.
 */
fun Float.mapValueToRange(initialHeaderPosY: Float): Float {
    val maxValue = 0f
    val mappedValue = (this - initialHeaderPosY) / (maxValue - initialHeaderPosY)
    return (1.0f - mappedValue).coerceIn(0f, 1f)
}

fun Long?.isValidValue(): Boolean {
    return this != null && this > 0
}

fun Long.toFormattedCurrency(): String {
    return "TODO-FORMAT"
//    return try {
//        val decimalFormat = (NumberFormat.getCurrencyInstance(Locale.US)).apply {
//            maximumFractionDigits = 0
//        }
//        decimalFormat.format(this)
//    } catch (e: IllegalArgumentException) {
//        ""
//    }
}
