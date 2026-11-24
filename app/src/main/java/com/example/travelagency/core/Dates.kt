package com.example.travelagency.core

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val DISPLAY_PATTERN = "dd.MM.yyyy"

private fun displayFormat() = SimpleDateFormat(DISPLAY_PATTERN, Locale.US)

fun Long.asDate(): String = displayFormat().format(Date(this))

/** "14.03.1990" -> epoch millis, либо null при неверном формате. */
fun parseDate(text: String): Long? {
    if (text.length != DISPLAY_PATTERN.length) return null
    return try {
        val format = displayFormat()
        format.isLenient = false
        format.parse(text)?.time
    } catch (e: ParseException) {
        null
    }
}
