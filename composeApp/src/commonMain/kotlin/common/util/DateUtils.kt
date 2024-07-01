package common.util

import common.domain.util.Constants.MONTH_PERIOD_COMING_SOON

expect object DateUtils {
    fun getComingSoonDates(
        monthPeriod: Int = MONTH_PERIOD_COMING_SOON,
    ): Pair<String, String>
    fun getCurrentTimeMillis(): Long
}
