package common.util

import platform.Foundation.NSLocale
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle
import platform.Foundation.localeWithLocaleIdentifier

actual object StringFormat {
    actual fun formatRating(number: Double): String {
        val formatter = NSNumberFormatter()
        formatter.minimumFractionDigits = 1u
        formatter.maximumFractionDigits = 1u
        formatter.numberStyle = 1u
        return formatter.stringFromNumber(NSNumber(number))!!
    }

    actual fun Long.toFormattedCurrency(): String {
        return try {
            val numberFormatter = NSNumberFormatter().apply {
                numberStyle = NSNumberFormatterCurrencyStyle
                locale = NSLocale.localeWithLocaleIdentifier("en_US")
                maximumFractionDigits = 0u
            }
            val nsNumber = NSNumber(this.toDouble())
            numberFormatter.stringFromNumber(nsNumber) ?: ""
        } catch (e: Exception) {
            ""
        }
    }
}
