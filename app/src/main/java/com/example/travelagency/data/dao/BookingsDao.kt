package com.example.travelagency.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.travelagency.data.entity.BookingEntity
import com.example.travelagency.data.entity.ContractEntity
import com.example.travelagency.data.entity.ContractStatus
import com.example.travelagency.data.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

/** Проекция строки брони: клиент и тур через JOIN. */
data class BookingListItem(
    val id: Long,
    val clientId: Long,
    val clientName: String,
    val clientPhone: String,
    val tourId: Long,
    val tourName: String,
    val destination: String,
    val touristsCount: Int,
    val totalPriceMinor: Long,
    val createdAt: Long,
    val status: String,
)

@Dao
interface BookingsDao {
    @Query(
        """
        SELECT b.id, b.clientId,
               c.lastName || ' ' || c.firstName || ' ' || c.middleName AS clientName,
               c.phone AS clientPhone,
               b.tourId, t.name AS tourName,
               d.country || ', ' || d.city AS destination,
               b.touristsCount, b.totalPriceMinor, b.createdAt, b.status
        FROM bookings b
        JOIN clients c ON c.id = b.clientId
        JOIN tours t ON t.id = b.tourId
        JOIN destinations d ON d.id = t.destinationId
        WHERE (:status = 'ALL' OR b.status = :status)
        ORDER BY b.createdAt DESC, b.id DESC
        """,
    )
    fun observeAll(status: String): Flow<List<BookingListItem>>

    @Query(
        """
        SELECT b.id, b.clientId,
               c.lastName || ' ' || c.firstName || ' ' || c.middleName AS clientName,
               c.phone AS clientPhone,
               b.tourId, t.name AS tourName,
               d.country || ', ' || d.city AS destination,
               b.touristsCount, b.totalPriceMinor, b.createdAt, b.status
        FROM bookings b
        JOIN clients c ON c.id = b.clientId
        JOIN tours t ON t.id = b.tourId
        JOIN destinations d ON d.id = t.destinationId
        WHERE b.clientId = :clientId
        ORDER BY b.createdAt DESC, b.id DESC
        """,
    )
    fun observeByClient(clientId: Long): Flow<List<BookingListItem>>

    @Query("SELECT * FROM bookings WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): BookingEntity?

    @Insert
    suspend fun insert(booking: BookingEntity): Long

    @Update
    suspend fun update(booking: BookingEntity)

    @Query("UPDATE bookings SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("DELETE FROM bookings WHERE id = :id")
    suspend fun deleteById(id: Long)

    /**
     * Оформление брони одним целостным действием: бронь + договор + предоплата.
     * Транзакция по образцу «перевода между счетами» из лекции 08:
     * либо записи появляются вместе, либо никак.
     */
    @Transaction
    suspend fun createBookingWithDocuments(
        booking: BookingEntity,
        contractNumberPrefix: String,
        prepayment: PaymentEntity?,
    ): Long {
        require(booking.touristsCount in MIN_TOURISTS..MAX_TOURISTS) { "tourists out of range" }
        val bookingId = insert(booking)
        val contractId = insertContract(
            ContractEntity(
                bookingId = bookingId,
                number = "$contractNumberPrefix-$bookingId",
                concludedAt = booking.createdAt,
                amountMinor = booking.totalPriceMinor,
            ),
        )
        prepayment?.let { payment ->
            insertPayment(payment.copy(contractId = contractId))
        }
        return contractId
    }

    /** Расторжение договора и отмена брони выполняются вместе (транзакция). */
    @Transaction
    suspend fun cancelBookingWithContract(bookingId: Long) {
        updateContractStatusByBooking(bookingId, ContractStatus.CANCELLED.name)
        updateStatus(bookingId, com.example.travelagency.data.entity.BookingStatus.CANCELLED.name)
    }

    @Query("UPDATE contracts SET status = :status WHERE bookingId = :bookingId")
    suspend fun updateContractStatusByBooking(bookingId: Long, status: String)

    @Insert
    suspend fun insertContract(contract: ContractEntity): Long

    @Insert
    suspend fun insertPayment(payment: PaymentEntity): Long

    companion object {
        const val MIN_TOURISTS = 1
        const val MAX_TOURISTS = 10
    }
}
