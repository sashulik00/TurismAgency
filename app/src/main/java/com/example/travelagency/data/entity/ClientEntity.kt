package com.example.travelagency.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val lastName: String,
    val firstName: String,
    val middleName: String,
    val birthDate: Long,
    val passportSeries: String,
    val passportNumber: String,
    val phone: String,
    val email: String,
) {
    val fullName: String get() = "$lastName $firstName $middleName".trim()
}
