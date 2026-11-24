package com.example.travelagency.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "tour_operators")
data class TourOperatorEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val contactPerson: String,
    val phone: String,
    val email: String,
)

@Entity(tableName = "destinations")
data class DestinationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val country: String,
    val city: String,
) {
    val title: String get() = "$country, $city"
}

@Entity(
    tableName = "hotels",
    foreignKeys = [
        ForeignKey(
            entity = DestinationEntity::class,
            parentColumns = ["id"],
            childColumns = ["destinationId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("destinationId")],
)
data class HotelEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val stars: Int,
    val destinationId: Long,
)

@Entity(tableName = "meal_types")
data class MealTypeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val name: String,
)
