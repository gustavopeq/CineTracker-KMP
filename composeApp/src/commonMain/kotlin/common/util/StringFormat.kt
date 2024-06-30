package common.util

expect object StringFormat {
    fun formatRating(number: Double): String
    fun Long.toFormattedCurrency(): String
}
