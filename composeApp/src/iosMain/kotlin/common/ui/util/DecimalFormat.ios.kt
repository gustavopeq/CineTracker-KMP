package common.ui.util

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter

actual class DecimalFormat {
    actual fun format(number: Double): String {
        val formatter = NSNumberFormatter()
        formatter.minimumFractionDigits = 1u
        formatter.maximumFractionDigits = 1u
        formatter.numberStyle = 1u
        return formatter.stringFromNumber(NSNumber(number))!!
    }
}
