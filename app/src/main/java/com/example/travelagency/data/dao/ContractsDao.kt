package com.example.travelagency.data.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/** Проекция договора с агрегатами платежей (подзапросы SUM по образцу лекции 08). */
data class ContractListItem(
    val id: Long,
    val bookingId: Long,
    val number: String,
    val concludedAt: Long,
    val amountMinor: Long,
    val status: String,
    val clientName: String,
    val clientPhone: String,
    val tourName: String,
    val destination: String,
    val paidMinor: Long,
    val refundedMinor: Long,
)

@Dao
interface ContractsDao {
    @Query(
        """
        SELECT c.id, c.bookingId, c.number, c.concludedAt, c.amountMinor, c.status,
               cl.lastName || ' ' || cl.firstName || ' ' || cl.middleName AS clientName,
               cl.phone AS clientPhone,
               t.name AS tourName,
               d.country || ', ' || d.city AS destination,
               IFNULL((SELECT SUM(p.amountMinor) FROM payments p
                        WHERE p.contractId = c.id AND p.kind != 'REFUND'), 0) AS paidMinor,
               IFNULL((SELECT SUM(p.amountMinor) FROM payments p
                        WHERE p.contractId = c.id AND p.kind = 'REFUND'), 0) AS refundedMinor
        FROM contracts c
        JOIN bookings b ON b.id = c.bookingId
        JOIN clients cl ON cl.id = b.clientId
        JOIN tours t ON t.id = b.tourId
        JOIN destinations d ON d.id = t.destinationId
        ORDER BY c.concludedAt DESC, c.id DESC
        """,
    )
    fun observeAll(): Flow<List<ContractListItem>>

    @Query(
        """
        SELECT c.id, c.bookingId, c.number, c.concludedAt, c.amountMinor, c.status,
               cl.lastName || ' ' || cl.firstName || ' ' || cl.middleName AS clientName,
               cl.phone AS clientPhone,
               t.name AS tourName,
               d.country || ', ' || d.city AS destination,
               IFNULL((SELECT SUM(p.amountMinor) FROM payments p
                        WHERE p.contractId = c.id AND p.kind != 'REFUND'), 0) AS paidMinor,
               IFNULL((SELECT SUM(p.amountMinor) FROM payments p
                        WHERE p.contractId = c.id AND p.kind = 'REFUND'), 0) AS refundedMinor
        FROM contracts c
        JOIN bookings b ON b.id = c.bookingId
        JOIN clients cl ON cl.id = b.clientId
        JOIN tours t ON t.id = b.tourId
        JOIN destinations d ON d.id = t.destinationId
        WHERE c.id = :contractId
        """,
    )
    fun observeDetails(contractId: Long): Flow<ContractListItem?>
}
