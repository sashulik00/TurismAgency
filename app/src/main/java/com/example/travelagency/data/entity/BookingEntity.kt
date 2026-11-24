package com.example.travelagency.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bookings",
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = TourEntity::class,
            parentColumns = ["id"],
            childColumns = ["tourId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("clientId"), Index("tourId")],
)
data class BookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clientId: Long,
    val tourId: Long,
    val touristsCount: Int,
    val totalPriceMinor: Long,
    val createdAt: Long,
    val status: BookingStatus = BookingStatus.PENDING,
)
