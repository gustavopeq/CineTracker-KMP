package common.util

import common.domain.util.Constants.MONTH_PERIOD_COMING_SOON

interface DateUtility {
    fun getComingSoonDates(
        monthPeriod: Int = MONTH_PERIOD_COMING_SOON,
    ): Pair<String, String>
}

expect object DateUtils : DateUtility
