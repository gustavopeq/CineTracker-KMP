package common.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale

actual object StringFormat {
    actual fun formatRating(number: Double): String {
        val symbols = DecimalFormatSymbols.getInstance().apply {
            decimalSeparator = '.'
        }
        var formattedRating = DecimalFormat("#.#", symbols).format(number)
        if (formattedRating.length == 1) {
            formattedRating += "${symbols.decimalSeparator}0"
        }
        return formattedRating
    }

    actual fun Long.toFormattedCurrency(): String {
        return try {
            val decimalFormat = (NumberFormat.getCurrencyInstance(Locale.US)).apply {
                maximumFractionDigits = 0
            }
            decimalFormat.format(this)
        } catch (e: IllegalArgumentException) {
            ""
        }
    }
}
