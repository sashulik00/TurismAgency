package com.example.travelagency.core

import java.security.MessageDigest

/** Хеширование пароля SHA-256 (требование защиты информации из ТЗ). */
fun String.sha256(): String =
    MessageDigest.getInstance("SHA-256")
        .digest(toByteArray(Charsets.UTF_8))
        .joinToString(separator = "") { byte -> String.format("%02x", byte) }
