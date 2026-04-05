package common.util

import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.notif_friday_1
import cinetracker_kmp.composeapp.generated.resources.notif_friday_2
import cinetracker_kmp.composeapp.generated.resources.notif_friday_3
import cinetracker_kmp.composeapp.generated.resources.notif_sunday_1
import cinetracker_kmp.composeapp.generated.resources.notif_sunday_2
import cinetracker_kmp.composeapp.generated.resources.notif_sunday_3
import org.jetbrains.compose.resources.StringResource

object EngagementMessages {
    const val FRIDAY_DAY_OF_WEEK = 1
    const val SUNDAY_DAY_OF_WEEK = 1
    const val FRIDAY_HOUR = 18
    const val SUNDAY_HOUR = 19
    const val NOTIFICATION_MINUTE = 13

    fun getFridayMessages(): List<StringResource> = listOf(
        Res.string.notif_sunday_1,
//        Res.string.notif_friday_2,
//        Res.string.notif_friday_3
    )

    fun getSundayMessages(): List<StringResource> = listOf(
        Res.string.notif_sunday_1,
        Res.string.notif_sunday_2,
        Res.string.notif_sunday_3
    )

    fun getMessagesForDayOfWeek(dayOfWeek: Int): List<StringResource> = when (dayOfWeek) {
        FRIDAY_DAY_OF_WEEK -> getFridayMessages()
        SUNDAY_DAY_OF_WEEK -> getSundayMessages()
        else -> emptyList()
    }

    fun getRandomMessageForDayOfWeek(dayOfWeek: Int): StringResource? =
        getMessagesForDayOfWeek(dayOfWeek).randomOrNull()
}
