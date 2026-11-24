package com.example.travelagency

import com.example.travelagency.core.asRubles
import com.example.travelagency.core.bookingTotal
import com.example.travelagency.core.groupDigits
import com.example.travelagency.core.parseRubles
import com.example.travelagency.core.prepaymentOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MoneyTest {

    @Test
    fun `rubles format without kopecks`() {
        assertEquals("12 500 ₽", 1_250_000L.asRubles())
    }

    @Test
    fun `rubles format with kopecks`() {
        assertEquals("12 500,50 ₽", 1_250_050L.asRubles())
    }

    @Test
    fun `zero and small amounts`() {
        assertEquals("0 ₽", 0L.asRubles())
        assertEquals("0,99 ₽", 99L.asRubles())
    }

    @Test
    fun `negative amount keeps minus`() {
        assertEquals("-1 500 ₽", (-150_000L).asRubles())
    }

    @Test
    fun `group digits adds spaces`() {
        assertEquals("1 234 567", groupDigits(1_234_567L))
    }

    @Test
    fun `parse rubles to minor units`() {
        assertEquals(1_250_000L, parseRubles("12500"))
        assertEquals(1_250_000L, parseRubles("12 500"))
        assertEquals(150_550L, parseRubles("1505.5"))
    }

    @Test
    fun `parse rubles rejects invalid input`() {
        assertNull(parseRubles(""))
        assertNull(parseRubles("abc"))
        assertNull(parseRubles("-100"))
        assertNull(parseRubles("0"))
    }

    @Test
    fun `booking total multiplies price by tourists`() {
        assertEquals(297_000_000L, bookingTotal(99_000_000L, 3))
    }

    @Test
    fun `prepayment is thirty percent rounded up`() {
        assertEquals(29_700_000L, prepaymentOf(99_000_000L))
        assertEquals(1L, prepaymentOf(1L))
    }
}
