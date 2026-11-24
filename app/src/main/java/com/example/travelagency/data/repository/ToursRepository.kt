package com.example.travelagency.data.repository

import com.example.travelagency.data.dao.DestinationsDao
import com.example.travelagency.data.dao.TourDetailsRow
import com.example.travelagency.data.dao.TourListItem
import com.example.travelagency.data.dao.ToursDao
import com.example.travelagency.data.entity.DestinationEntity
import com.example.travelagency.data.entity.TourEntity
import com.example.travelagency.data.entity.TourStatus
import kotlinx.coroutines.flow.Flow

/** Репозиторий между ViewModel и DAO — по схеме лекции 08 и практики 08. */
class ToursRepository(
    private val toursDao: ToursDao,
    private val destinationsDao: DestinationsDao,
) {
    fun observeTours(query: String, onlyActive: Boolean, destinationId: Long): Flow<List<TourListItem>> =
        toursDao.observeAll(query, onlyActive, destinationId)

    fun observeTourDetails(tourId: Long): Flow<TourDetailsRow?> = toursDao.observeDetails(tourId)

    fun observeDestinations(): Flow<List<DestinationEntity>> = destinationsDao.observeAll("")

    suspend fun findTour(tourId: Long): TourEntity? = toursDao.findById(tourId)

    /** Создание или обновление тура администратором. */
    suspend fun save(tour: TourEntity): Long = if (tour.id == 0L) {
        toursDao.insert(tour)
    } else {
        toursDao.update(tour)
        tour.id
    }

    suspend fun setDiscontinued(tourId: Long) = toursDao.updateStatus(tourId, TourStatus.DISCONTINUED.name)

    suspend fun setActive(tourId: Long) = toursDao.updateStatus(tourId, TourStatus.ACTIVE.name)
}
