package com.example.travelagency.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.travelagency.data.dao.AuditDao
import com.example.travelagency.data.dao.BookingsDao
import com.example.travelagency.data.dao.ClientsDao
import com.example.travelagency.data.dao.ContractsDao
import com.example.travelagency.data.dao.Converters
import com.example.travelagency.data.dao.DestinationsDao
import com.example.travelagency.data.dao.EmployeesDao
import com.example.travelagency.data.dao.HotelsDao
import com.example.travelagency.data.dao.MealTypesDao
import com.example.travelagency.data.dao.OperatorsDao
import com.example.travelagency.data.dao.PaymentsDao
import com.example.travelagency.data.dao.ToursDao
import com.example.travelagency.data.entity.AuditEntryEntity
import com.example.travelagency.data.entity.BookingEntity
import com.example.travelagency.data.entity.ClientEntity
import com.example.travelagency.data.entity.ContractEntity
import com.example.travelagency.data.entity.DestinationEntity
import com.example.travelagency.data.entity.EmployeeEntity
import com.example.travelagency.data.entity.HotelEntity
import com.example.travelagency.data.entity.MealTypeEntity
import com.example.travelagency.data.entity.PaymentEntity
import com.example.travelagency.data.entity.TourEntity
import com.example.travelagency.data.entity.TourOperatorEntity

@Database(
    entities = [
        EmployeeEntity::class,
        ClientEntity::class,
        TourOperatorEntity::class,
        DestinationEntity::class,
        HotelEntity::class,
        MealTypeEntity::class,
        TourEntity::class,
        BookingEntity::class,
        ContractEntity::class,
        PaymentEntity::class,
        AuditEntryEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun employeesDao(): EmployeesDao
    abstract fun clientsDao(): ClientsDao
    abstract fun toursDao(): ToursDao
    abstract fun bookingsDao(): BookingsDao
    abstract fun contractsDao(): ContractsDao
    abstract fun paymentsDao(): PaymentsDao
    abstract fun operatorsDao(): OperatorsDao
    abstract fun destinationsDao(): DestinationsDao
    abstract fun hotelsDao(): HotelsDao
    abstract fun mealTypesDao(): MealTypesDao
    abstract fun auditDao(): AuditDao

    companion object {
        const val NAME = "travel_agency.db"
    }
}
