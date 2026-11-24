package com.example.travelagency.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Заполняется триггером trg_bookings_audit при смене статуса брони. */
@Entity(tableName = "audit_log")
data class AuditEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val entity: String,
    val entityId: Long,
    val action: String,
    val details: String,
    val at: Long,
)
