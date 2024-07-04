package common.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.april
import cinetracker_kmp.composeapp.generated.resources.august
import cinetracker_kmp.composeapp.generated.resources.december
import cinetracker_kmp.composeapp.generated.resources.february
import cinetracker_kmp.composeapp.generated.resources.january
import cinetracker_kmp.composeapp.generated.resources.july
import cinetracker_kmp.composeapp.generated.resources.june
import cinetracker_kmp.composeapp.generated.resources.march
import cinetracker_kmp.composeapp.generated.resources.may
import cinetracker_kmp.composeapp.generated.resources.november
import cinetracker_kmp.composeapp.generated.resources.october
import cinetracker_kmp.composeapp.generated.resources.september
import cinetracker_kmp.composeapp.generated.resources.undefined_ratings
import cinetracker_kmp.composeapp.generated.resources.unknown
import common.util.UiConstants.EMPTY_RATINGS
import common.util.platform.StringFormat
import org.jetbrains.compose.resources.stringResource

@Composable
fun String.formatDate(): String {
    val month: Int?
    val day: Int?
    val year: String

    try {
        month = this.substring(5, 7).toIntOrNull()
        day = this.substring(8, 10).toIntOrNull()
        year = this.substring(0, 4)
    } catch (e: Exception) {
        println("Date format caught exception: $e")
        return stringResource(Res.string.unknown)
    }

    val monthFormated: String? = when (month) {
        1 -> stringResource(Res.string.january)
        2 -> stringResource(Res.string.february)
        3 -> stringResource(Res.string.march)
        4 -> stringResource(Res.string.april)
        5 -> stringResource(Res.string.may)
        6 -> stringResource(Res.string.june)
        7 -> stringResource(Res.string.july)
        8 -> stringResource(Res.string.august)
        9 -> stringResource(Res.string.september)
        10 -> stringResource(Res.string.october)
        11 -> stringResource(Res.string.november)
        12 -> stringResource(Res.string.december)
        else -> null
    }

    return if (monthFormated != null && day != null) {
        "$monthFormated $day, $year"
    } else {
        stringResource(Res.string.unknown)
    }
}

@Composable
fun Double?.formatRating(): String {
    if (this == null || this == EMPTY_RATINGS) {
        return stringResource(resource = Res.string.undefined_ratings)
    }

    return StringFormat.formatRating(this)
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
