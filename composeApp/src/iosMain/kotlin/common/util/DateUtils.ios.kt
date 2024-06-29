package common.util

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

actual object DateUtils : DateUtility {
    override fun getComingSoonDates(
        monthPeriod: Int,
    ): Pair<String, String> {
        val calendar = NSCalendar.currentCalendar

        val dateFormatter = NSDateFormatter().apply {
            dateFormat = "yyyy-MM-dd"
            locale = NSLocale.currentLocale
        }
        val releaseDateGte = dateFormatter.stringFromDate(NSDate())
        val releaseDateLte = calendar.dateByAddingUnit(
            NSCalendarUnitMonth, monthPeriod.toLong(), NSDate(), 0u,
        )?.let {
            dateFormatter.stringFromDate(it)
        } ?: releaseDateGte
        return Pair(releaseDateGte, releaseDateLte)
    }
}
