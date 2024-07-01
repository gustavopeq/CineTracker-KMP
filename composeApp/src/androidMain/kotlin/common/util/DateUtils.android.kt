package common.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

actual object DateUtils {
    actual fun getComingSoonDates(
        monthPeriod: Int,
    ): Pair<String, String> {
        val calendar = Calendar.getInstance()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val releaseDateGte = dateFormat.format(calendar.time)
        calendar.add(Calendar.MONTH, monthPeriod)
        val releaseDateLte = dateFormat.format(calendar.time)
        return Pair(releaseDateGte, releaseDateLte)
    }

    actual fun getCurrentTimeMillis(): Long = System.currentTimeMillis()
}
