package com.example.travelagency.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.travelagency.data.entity.DestinationEntity
import com.example.travelagency.data.entity.HotelEntity
import com.example.travelagency.data.entity.MealTypeEntity
import com.example.travelagency.data.entity.TourOperatorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OperatorsDao {
    @Query("SELECT * FROM tour_operators ORDER BY name")
    fun observeAll(): Flow<List<TourOperatorEntity>>

    @Query("SELECT * FROM tour_operators WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): TourOperatorEntity?

    @Insert
    suspend fun insert(operator: TourOperatorEntity): Long

    @Update
    suspend fun update(operator: TourOperatorEntity)

    @Query("DELETE FROM tour_operators WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface DestinationsDao {
    @Query(
        """
        SELECT * FROM destinations
        WHERE (:query = '' OR country LIKE '%' || :query || '%' OR city LIKE '%' || :query || '%')
        ORDER BY country, city
        """,
    )
    fun observeAll(query: String): Flow<List<DestinationEntity>>

    @Query("SELECT * FROM destinations ORDER BY country, city")
    suspend fun getAll(): List<DestinationEntity>

    @Insert
    suspend fun insert(destination: DestinationEntity): Long

    @Update
    suspend fun update(destination: DestinationEntity)

    @Query("DELETE FROM destinations WHERE id = :id")
    suspend fun deleteById(id: Long)
}

/** Проекция отеля с направлением — LEFT JOIN не теряет отели (лекция 08). */
data class HotelListItem(
    val id: Long,
    val name: String,
    val stars: Int,
    val destinationId: Long,
    val destinationTitle: String,
)

@Dao
interface HotelsDao {
    @Query(
        """
        SELECT h.id, h.name, h.stars, h.destinationId,
               d.country || ', ' || d.city AS destinationTitle
        FROM hotels h
        JOIN destinations d ON d.id = h.destinationId
        WHERE (:query = '' OR h.name LIKE '%' || :query || '%' OR d.country LIKE '%' || :query || '%')
        ORDER BY d.country, h.name
        """,
    )
    fun observeAll(query: String): Flow<List<HotelListItem>>

    @Query("SELECT * FROM hotels WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): HotelEntity?

    @Query("SELECT * FROM hotels ORDER BY name")
    suspend fun getAll(): List<HotelEntity>

    @Insert
    suspend fun insert(hotel: HotelEntity): Long

    @Update
    suspend fun update(hotel: HotelEntity)

    @Query("DELETE FROM hotels WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface MealTypesDao {
    @Query("SELECT * FROM meal_types ORDER BY code")
    fun observeAll(): Flow<List<MealTypeEntity>>

    @Insert
    suspend fun insert(mealType: MealTypeEntity): Long

    @Update
    suspend fun update(mealType: MealTypeEntity)

    @Query("DELETE FROM meal_types WHERE id = :id")
    suspend fun deleteById(id: Long)
}
