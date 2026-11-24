package com.example.travelagency.core

private val NAME_REGEX = Regex("^[А-Яа-яЁёA-Za-z-]{1,30}$")
private val PHONE_REGEX = Regex("^\\+7\\d{10}$")
private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
private val PASSPORT_SERIES_REGEX = Regex("^\\d{4}$")
private val PASSPORT_NUMBER_REGEX = Regex("^\\d{6}$")

fun isValidName(value: String): Boolean = value.trim().matches(NAME_REGEX)

fun isValidPhone(value: String): Boolean = value.trim().matches(PHONE_REGEX)

fun isValidEmail(value: String): Boolean = value.trim().matches(EMAIL_REGEX)

fun isValidPassport(series: String, number: String): Boolean =
    series.trim().matches(PASSPORT_SERIES_REGEX) && number.trim().matches(PASSPORT_NUMBER_REGEX)

/** Годы должны быть в разумных пределах: не будущая дата. */
fun isAdultBirthDate(date: Long): Boolean = date in MIN_BIRTH_DATE..System.currentTimeMillis()

const val MIN_BIRTH_DATE = -2_208_988_800_000L // 01.01.1900
