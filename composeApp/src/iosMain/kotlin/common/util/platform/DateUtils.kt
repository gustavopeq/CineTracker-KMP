package common.util.platform

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.timeIntervalSince1970

actual object DateUtils {
    actual fun getComingSoonDates(
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

    actual fun getCurrentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()
}
