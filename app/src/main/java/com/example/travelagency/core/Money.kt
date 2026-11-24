package com.example.travelagency.core

private const val MINOR_PER_MAJOR = 100
private const val GROUP_SIZE = 3

/**
 * Все суммы в приложении хранятся в копейках (Long) — как amount_minor в лекции 08.
 * Функции-расширения оформлены по образцу formattedAmount из лекции 04.
 */
fun Long.asRubles(): String {
    val rubles = this / MINOR_PER_MAJOR
    val kopecks = Math.abs(this % MINOR_PER_MAJOR)
    val grouped = groupDigits(rubles)
    if (kopecks == 0L) return "$grouped ₽"
    val kopecksText = if (kopecks < 10) "0$kopecks" else kopecks.toString()
    return "$grouped,$kopecksText ₽"
}

fun groupDigits(value: Long): String {
    val digits = Math.abs(value).toString()
    val builder = StringBuilder()
    for (index in digits.indices) {
        builder.append(digits[index])
        val remaining = digits.length - index - 1
        if (remaining > 0 && remaining % GROUP_SIZE == 0) {
            builder.append(' ')
        }
    }
    return if (value < 0) "-$builder" else builder.toString()
}

/** Разбор суммы в рублях, введённой пользователем: "12500" -> 1 250 000 копеек. */
fun parseRubles(text: String): Long? {
    val cleaned = text.replace(" ", "").replace(',', '.')
    if (cleaned.isEmpty()) return null
    val value = cleaned.toDoubleOrNull() ?: return null
    if (value <= 0.0) return null
    return Math.round(value * MINOR_PER_MAJOR)
}

/** Стоимость брони: цена тура * количество туристов (лекция 04, чистая функция). */
fun bookingTotal(priceMinor: Long, tourists: Int): Long = priceMinor * tourists

/** Предоплата 30% от суммы — округление вверх до целой копейки. */
fun prepaymentOf(totalMinor: Long): Long = Math.ceil(totalMinor * PREPAYMENT_RATE).toLong()

private const val PREPAYMENT_RATE = 0.3
