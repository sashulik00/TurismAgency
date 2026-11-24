package com.example.travelagency

import com.example.travelagency.core.isValidEmail
import com.example.travelagency.core.isValidName
import com.example.travelagency.core.isValidPassport
import com.example.travelagency.core.isValidPhone
import com.example.travelagency.core.parseDate
import com.example.travelagency.core.sha256
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class ValidationTest {

    @Test
    fun `valid cyrillic and latin names`() {
        assertTrue(isValidName("Петров"))
        assertTrue(isValidName("Smith-Jones"))
        assertFalse(isValidName(""))
        assertFalse(isValidName("Петров123"))
        assertFalse(isValidName("Петров Дмитрий"))
    }

    @Test
    fun `phone requires plus7 and ten digits`() {
        assertTrue(isValidPhone("+79171234567"))
        assertFalse(isValidPhone("89171234567"))
        assertFalse(isValidPhone("+7917123456"))
        assertFalse(isValidPhone("+791712345678"))
    }

    @Test
    fun `email basic format`() {
        assertTrue(isValidEmail("petrov@mail.ru"))
        assertFalse(isValidEmail("petrov"))
        assertFalse(isValidEmail("petrov@"))
    }

    @Test
    fun `passport series four digits number six digits`() {
        assertTrue(isValidPassport("9203", "456123"))
        assertFalse(isValidPassport("920", "456123"))
        assertFalse(isValidPassport("9203", "45612"))
        assertFalse(isValidPassport("920a", "456123"))
    }

    @Test
    fun `parse date accepts only ddMMyyyy`() {
        assertNotNull(parseDate("14.03.1990"))
        assertNull(parseDate("14.3.1990"))
        assertNull(parseDate("32.12.1990"))
        assertNull(parseDate("1990-03-14"))
    }

    @Test
    fun `parse date epoch value`() {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.set(1990, Calendar.MARCH, 14)
        assertEquals(calendar.timeInMillis, parseDate("14.03.1990"))
    }

    @Test
    fun `sha256 is deterministic and hex`() {
        val hash = "admin".sha256()
        assertEquals(64, hash.length)
        assertTrue(hash.all { it in "0123456789abcdef" })
        assertEquals(hash, "admin".sha256())
    }
}
