package common.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

actual object DecimalFormat {
    actual fun format(number: Double): String {
        val symbols = DecimalFormatSymbols.getInstance().apply {
            decimalSeparator = '.'
        }
        var formattedRating = DecimalFormat("#.#", symbols).format(number)
        if (formattedRating.length == 1) {
            formattedRating += "${symbols.decimalSeparator}0"
        }
        return formattedRating
    }
}
