package com.example.travelagency.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.travelagency.data.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentsDao {
    /** Room отслеживает инвалидирование таблиц — Flow обновляется сам (лекция 08). */
    @Query("SELECT * FROM payments WHERE contractId = :contractId ORDER BY paidAt DESC, id DESC")
    fun observeByContract(contractId: Long): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): PaymentEntity?

    @Insert
    suspend fun insert(payment: PaymentEntity): Long

    @Query("DELETE FROM payments WHERE id = :id")
    suspend fun deleteById(id: Long)
}
