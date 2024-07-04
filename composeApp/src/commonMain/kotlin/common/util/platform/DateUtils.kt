package common.util.platform

import common.util.Constants

expect object DateUtils {
    fun getComingSoonDates(
        monthPeriod: Int = Constants.MONTH_PERIOD_COMING_SOON,
    ): Pair<String, String>
    fun getCurrentTimeMillis(): Long
}
