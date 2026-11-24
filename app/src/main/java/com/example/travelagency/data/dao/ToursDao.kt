package com.example.travelagency.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.travelagency.data.entity.TourEntity
import kotlinx.coroutines.flow.Flow

/** Проекция карточки тура для списка — JOIN по справочникам (как TransactionRow в лекции 08). */
data class TourListItem(
    val id: Long,
    val name: String,
    val country: String,
    val city: String,
    val hotelName: String,
    val stars: Int,
    val mealName: String,
    val operatorName: String?,
    val departureDate: Long,
    val nights: Int,
    val priceMinor: Long,
    val quota: Int,
    val status: String,
)

data class TourDetailsRow(
    val id: Long,
    val name: String,
    val description: String,
    val country: String,
    val city: String,
    val hotelName: String,
    val stars: Int,
    val mealName: String,
    val mealCode: String,
    val operatorName: String?,
    val operatorPhone: String?,
    val departureDate: Long,
    val nights: Int,
    val priceMinor: Long,
    val quota: Int,
    val status: String,
)

private const val TOUR_FIELDS = """
    t.id, t.name, t.description, t.departureDate, t.nights, t.priceMinor, t.quota, t.status,
    d.country, d.city, h.name AS hotelName, h.stars,
    m.name AS mealName, m.code AS mealCode,
    o.name AS operatorName, o.phone AS operatorPhone
"""

private const val TOUR_JOINS = """
    FROM tours t
    JOIN destinations d ON d.id = t.destinationId
    JOIN hotels h ON h.id = t.hotelId
    JOIN meal_types m ON m.id = t.mealTypeId
    LEFT JOIN tour_operators o ON o.id = t.operatorId
"""

@Dao
interface ToursDao {
    @Query(
        """
        SELECT t.id, t.name, d.country, d.city, h.name AS hotelName, h.stars,
               m.name AS mealName, o.name AS operatorName,
               t.departureDate, t.nights, t.priceMinor, t.quota, t.status
        $TOUR_JOINS
        WHERE (:query = '' OR t.name LIKE '%' || :query || '%'
               OR d.country LIKE '%' || :query || '%'
               OR d.city LIKE '%' || :query || '%'
               OR h.name LIKE '%' || :query || '%')
          AND (:onlyActive = 0 OR t.status = 'ACTIVE')
          AND (:destinationId = 0 OR t.destinationId = :destinationId)
        ORDER BY t.departureDate
        """,
    )
    fun observeAll(query: String, onlyActive: Boolean, destinationId: Long): Flow<List<TourListItem>>

    @Query("SELECT $TOUR_FIELDS $TOUR_JOINS WHERE t.id = :tourId")
    fun observeDetails(tourId: Long): Flow<TourDetailsRow?>

    @Query("SELECT * FROM tours WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): TourEntity?

    @Insert
    suspend fun insert(tour: TourEntity): Long

    @Update
    suspend fun update(tour: TourEntity)

    @Query("UPDATE tours SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("DELETE FROM tours WHERE id = :id")
    suspend fun deleteById(id: Long)
}
