package com.example.travelagency.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.travelagency.data.entity.ClientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientsDao {
    /** Поиск клиента по фамилии или телефону — LIKE-запрос с параметром, как в лекции 08. */
    @Query(
        """
        SELECT * FROM clients
        WHERE (:query = '' OR lastName LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%')
        ORDER BY lastName, firstName
        """,
    )
    fun observeAll(query: String): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): ClientEntity?

    @Query("SELECT * FROM clients WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<ClientEntity?>

    @Insert
    suspend fun insert(client: ClientEntity): Long

    @Update
    suspend fun update(client: ClientEntity)

    @Query("DELETE FROM clients WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM bookings WHERE clientId = :clientId")
    suspend fun countBookings(clientId: Long): Int
}
