package common.domain.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.undefined_ratings
import common.domain.util.UiConstants.EMPTY_RATINGS
import common.util.DecimalFormat
import org.jetbrains.compose.resources.stringResource

// TODO Fix Extensions
fun String.formatDate(): String {
    return "Fix format"
//    val month: Int?
//    val day: Int?
//    val year: String
//
//    try {
//        month = this.substring(5, 7).toIntOrNull()
//        day = this.substring(8, 10).toIntOrNull()
//        year = this.substring(0, 4)
//    } catch (e: StringIndexOutOfBoundsException) {
//        Timber.e("Date format caught exception: $e")
//        return context.resources.getString(R.string.unknown)
//    }
//
//    val monthFormated: String? = when (month) {
//        1 -> context.resources.getString(R.string.january)
//        2 -> context.resources.getString(R.string.february)
//        3 -> context.resources.getString(R.string.march)
//        4 -> context.resources.getString(R.string.april)
//        5 -> context.resources.getString(R.string.may)
//        6 -> context.resources.getString(R.string.june)
//        7 -> context.resources.getString(R.string.july)
//        8 -> context.resources.getString(R.string.august)
//        9 -> context.resources.getString(R.string.september)
//        10 -> context.resources.getString(R.string.october)
//        11 -> context.resources.getString(R.string.november)
//        12 -> context.resources.getString(R.string.december)
//        else -> null
//    }
//
//    return if (monthFormated != null && day != null) {
//        "$monthFormated $day, $year"
//    } else {
//        context.resources.getString(R.string.unknown)
//    }
}

@Composable
fun Double?.formatRating(): String {
    if (this == null || this == EMPTY_RATINGS) {
        return stringResource(resource = Res.string.undefined_ratings)
    }

    return DecimalFormat.format(this)
}

fun Modifier.removeParentPadding(
    paddingToRemove: Dp,
): Modifier {
    return this.layout { measurable, constraints ->
        val placeable = measurable.measure(
            constraints.copy(
                maxWidth = constraints.maxWidth + (paddingToRemove.roundToPx() * 2),
            ),
        )
        layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }
}

fun String.capitalized(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase() else it.toString()
    }
}
