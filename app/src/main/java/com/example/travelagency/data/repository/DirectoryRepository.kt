package com.example.travelagency.data.repository

import com.example.travelagency.data.dao.DestinationsDao
import com.example.travelagency.data.dao.EmployeesDao
import com.example.travelagency.data.dao.HotelsDao
import com.example.travelagency.data.dao.HotelListItem
import com.example.travelagency.data.dao.MealTypesDao
import com.example.travelagency.data.dao.OperatorsDao
import com.example.travelagency.data.dao.AuditDao
import com.example.travelagency.data.entity.AuditEntryEntity
import com.example.travelagency.data.entity.DestinationEntity
import com.example.travelagency.data.entity.EmployeeEntity
import com.example.travelagency.data.entity.HotelEntity
import com.example.travelagency.data.entity.MealTypeEntity
import com.example.travelagency.data.entity.TourOperatorEntity
import kotlinx.coroutines.flow.Flow

/** Справочники, сотрудники и журнал — раздел администратора. */
class DirectoryRepository(
    private val operatorsDao: OperatorsDao,
    private val destinationsDao: DestinationsDao,
    private val hotelsDao: HotelsDao,
    private val mealTypesDao: MealTypesDao,
    private val employeesDao: EmployeesDao,
    private val auditDao: AuditDao,
) {
    fun observeOperators(): Flow<List<TourOperatorEntity>> = operatorsDao.observeAll()
    fun observeDestinations(query: String): Flow<List<DestinationEntity>> = destinationsDao.observeAll(query)
    fun observeHotels(query: String): Flow<List<HotelListItem>> = hotelsDao.observeAll(query)
    fun observeMealTypes(): Flow<List<MealTypeEntity>> = mealTypesDao.observeAll()
    fun observeEmployees(): Flow<List<EmployeeEntity>> = employeesDao.observeAll()
    fun observeAudit(): Flow<List<AuditEntryEntity>> = auditDao.observeRecent()

    suspend fun saveOperator(item: TourOperatorEntity): Long = upsert(item.id, { operatorsDao.insert(item) }) {
        operatorsDao.update(item)
    }

    suspend fun saveDestination(item: DestinationEntity): Long = upsert(item.id, { destinationsDao.insert(item) }) {
        destinationsDao.update(item)
    }

    suspend fun saveHotel(item: HotelEntity): Long = upsert(item.id, { hotelsDao.insert(item) }) {
        hotelsDao.update(item)
    }

    suspend fun saveMealType(item: MealTypeEntity): Long = upsert(item.id, { mealTypesDao.insert(item) }) {
        mealTypesDao.update(item)
    }

    suspend fun saveEmployee(item: EmployeeEntity): Long = upsert(item.id, { employeesDao.insert(item) }) {
        employeesDao.update(item)
    }

    suspend fun deleteOperator(id: Long) = operatorsDao.deleteById(id)
    suspend fun deleteDestination(id: Long) = destinationsDao.deleteById(id)
    suspend fun deleteHotel(id: Long) = hotelsDao.deleteById(id)
    suspend fun deleteMealType(id: Long) = mealTypesDao.deleteById(id)
    suspend fun deleteEmployee(id: Long) = employeesDao.deleteById(id)

    suspend fun allDestinations(): List<DestinationEntity> = destinationsDao.getAll()

    suspend fun allHotels(): List<HotelEntity> = hotelsDao.getAll()

    private inline fun upsert(id: Long, insert: () -> Long, update: () -> Unit): Long =
        if (id == 0L) insert() else {
            update()
            id
        }
}
