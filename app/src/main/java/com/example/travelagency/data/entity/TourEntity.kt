package com.example.travelagency.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tours",
    foreignKeys = [
        ForeignKey(
            entity = TourOperatorEntity::class,
            parentColumns = ["id"],
            childColumns = ["operatorId"],
            onDelete = ForeignKey.SET_NULL,
        ),
        ForeignKey(
            entity = DestinationEntity::class,
            parentColumns = ["id"],
            childColumns = ["destinationId"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = HotelEntity::class,
            parentColumns = ["id"],
            childColumns = ["hotelId"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = MealTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["mealTypeId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("operatorId"), Index("destinationId"), Index("hotelId"), Index("mealTypeId")],
)
data class TourEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val operatorId: Long?,
    val destinationId: Long,
    val hotelId: Long,
    val mealTypeId: Long,
    val departureDate: Long,
    val nights: Int,
    val priceMinor: Long,
    val quota: Int,
    val description: String,
    val status: TourStatus = TourStatus.ACTIVE,
)
