package com.example.travelagency.data.repository

import com.example.travelagency.data.dao.ClientsDao
import com.example.travelagency.data.entity.ClientEntity
import kotlinx.coroutines.flow.Flow

class ClientsRepository(private val dao: ClientsDao) {
    fun observeClients(query: String): Flow<List<ClientEntity>> = dao.observeAll(query)

    fun observeClient(clientId: Long): Flow<ClientEntity?> = dao.observeById(clientId)

    suspend fun findClient(clientId: Long): ClientEntity? = dao.findById(clientId)

    suspend fun save(client: ClientEntity): Long =
        if (client.id == 0L) dao.insert(client) else {
            dao.update(client)
            client.id
        }

    /** Удаление запрещено внешним ключом, пока у клиента есть брони (RESTRICT). */
    suspend fun delete(clientId: Long) {
        check(dao.countBookings(clientId) == 0) { "CLIENT_HAS_BOOKINGS" }
        dao.deleteById(clientId)
    }
}
