package com.example.travelagency.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "contracts",
    foreignKeys = [
        ForeignKey(
            entity = BookingEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookingId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index(value = ["bookingId"], unique = true)],
)
data class ContractEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookingId: Long,
    val number: String,
    val concludedAt: Long,
    val amountMinor: Long,
    val status: ContractStatus = ContractStatus.ACTIVE,
)
