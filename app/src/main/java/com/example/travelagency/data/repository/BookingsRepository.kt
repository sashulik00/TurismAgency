package com.example.travelagency.data.repository

import com.example.travelagency.core.bookingTotal
import com.example.travelagency.core.prepaymentOf
import com.example.travelagency.data.dao.BookingListItem
import com.example.travelagency.data.dao.BookingsDao
import com.example.travelagency.data.entity.BookingEntity
import com.example.travelagency.data.entity.BookingStatus
import com.example.travelagency.data.entity.PaymentEntity
import com.example.travelagency.data.entity.PaymentKind
import com.example.travelagency.data.entity.PaymentMethod
import com.example.travelagency.data.entity.TourEntity
import kotlinx.coroutines.flow.Flow

class BookingsRepository(private val bookingsDao: BookingsDao) {
    fun observeBookings(status: String): Flow<List<BookingListItem>> = bookingsDao.observeAll(status)

    fun observeBookingsOfClient(clientId: Long): Flow<List<BookingListItem>> =
        bookingsDao.observeByClient(clientId)

    suspend fun findBooking(bookingId: Long): BookingEntity? = bookingsDao.findById(bookingId)

    /**
     * Оформление продажи: бронь + договор + предоплата одной транзакцией DAO.
     * Квота мест проверяется и списывается триггерами БД.
     */
    suspend fun createBooking(
        clientId: Long,
        tour: TourEntity,
        touristsCount: Int,
        withPrepayment: Boolean,
    ): Long {
        val now = System.currentTimeMillis()
        val total = bookingTotal(tour.priceMinor, touristsCount)
        val prepayment = if (withPrepayment) {
            PaymentEntity(
                contractId = 0,
                kind = PaymentKind.PREPAY,
                method = PaymentMethod.CARD,
                amountMinor = prepaymentOf(total),
                paidAt = now,
            )
        } else {
            null
        }
        return bookingsDao.createBookingWithDocuments(
            booking = BookingEntity(
                clientId = clientId,
                tourId = tour.id,
                touristsCount = touristsCount,
                totalPriceMinor = total,
                createdAt = now,
            ),
            contractNumberPrefix = CONTRACT_PREFIX,
            prepayment = prepayment,
        )
    }

    suspend fun updateStatus(bookingId: Long, status: BookingStatus) {
        bookingsDao.updateStatus(bookingId, status.name)
    }

    suspend fun cancelBookingWithContract(bookingId: Long) {
        bookingsDao.cancelBookingWithContract(bookingId)
    }

    suspend fun deleteBooking(bookingId: Long) {
        bookingsDao.deleteById(bookingId)
    }

    private companion object {
        const val CONTRACT_PREFIX = "Д-2026"
    }
}
