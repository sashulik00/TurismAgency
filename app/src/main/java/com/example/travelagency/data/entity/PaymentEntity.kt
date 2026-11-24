package com.example.travelagency.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = ContractEntity::class,
            parentColumns = ["id"],
            childColumns = ["contractId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("contractId")],
)
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contractId: Long,
    val kind: PaymentKind,
    val method: PaymentMethod,
    val amountMinor: Long,
    val paidAt: Long,
)
