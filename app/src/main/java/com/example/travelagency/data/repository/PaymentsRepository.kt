package com.example.travelagency.data.repository

import com.example.travelagency.data.dao.PaymentsDao
import com.example.travelagency.data.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

class PaymentsRepository(private val dao: PaymentsDao) {
    fun observeByContract(contractId: Long): Flow<List<PaymentEntity>> = dao.observeByContract(contractId)

    suspend fun findPayment(paymentId: Long): PaymentEntity? = dao.findById(paymentId)

    suspend fun save(payment: PaymentEntity): Long =
        if (payment.id == 0L) dao.insert(payment) else payment.id

    suspend fun delete(paymentId: Long) = dao.deleteById(paymentId)
}
