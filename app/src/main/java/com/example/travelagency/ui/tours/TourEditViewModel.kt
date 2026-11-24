package com.example.travelagency.ui.tours

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.travelagency.TravelAgencyApplication
import com.example.travelagency.core.asDate
import com.example.travelagency.core.parseDate
import com.example.travelagency.core.parseRubles
import com.example.travelagency.data.entity.DestinationEntity
import com.example.travelagency.data.entity.HotelEntity
import com.example.travelagency.data.entity.MealTypeEntity
import com.example.travelagency.data.entity.TourEntity
import com.example.travelagency.data.entity.TourOperatorEntity
import com.example.travelagency.data.entity.TourStatus
import com.example.travelagency.ui.navigation.TourEditRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TourFieldError { NAME, PICKERS, DEPARTURE, NIGHTS, PRICE, QUOTA }

data class TourEditUiState(
    val tourId: Long = 0,
    val isNew: Boolean = true,
    val name: String = "",
    val description: String = "",
    val departureText: String = "",
    val nightsText: String = "7",
    val priceText: String = "",
    val quotaText: String = "10",
    val operator: TourOperatorEntity? = null,
    val destination: DestinationEntity? = null,
    val hotel: HotelEntity? = null,
    val mealType: MealTypeEntity? = null,
    val operators: List<TourOperatorEntity> = emptyList(),
    val destinations: List<DestinationEntity> = emptyList(),
    val hotels: List<HotelEntity> = emptyList(),
    val mealTypes: List<MealTypeEntity> = emptyList(),
    val errors: List<TourFieldError> = emptyList(),
    val saved: Boolean = false,
)

/** Форма создания и редактирования тура для администратора. */
class TourEditViewModel(application: Application, savedStateHandle: SavedStateHandle) :
    AndroidViewModel(application) {
    private val container = (application as TravelAgencyApplication).container
    private val route = savedStateHandle.toRoute<TourEditRoute>()

    private val form = MutableStateFlow(TourEditUiState(tourId = route.tourId, isNew = route.tourId == 0L))

    init {
        viewModelScope.launch {
            val directory = container.directoryRepository
            val operators = directory.observeOperators().first()
            val destinations = directory.observeDestinations("").first()
            val hotels = directory.allHotels()
            val mealTypes = directory.observeMealTypes().first()
            var updated = form.value.copy(
                operators = operators,
                destinations = destinations,
                hotels = hotels,
                mealTypes = mealTypes,
            )
            if (route.tourId != 0L) {
                val tour = container.toursRepository.findTour(route.tourId)
                if (tour != null) {
                    updated = updated.copy(
                        name = tour.name,
                        description = tour.description,
                        departureText = tour.departureDate.asDate(),
                        nightsText = tour.nights.toString(),
                        priceText = (tour.priceMinor / 100).toString(),
                        quotaText = tour.quota.toString(),
                        operator = operators.firstOrNull { it.id == tour.operatorId },
                        destination = destinations.firstOrNull { it.id == tour.destinationId },
                        hotel = hotels.firstOrNull { it.id == tour.hotelId },
                        mealType = mealTypes.firstOrNull { it.id == tour.mealTypeId },
                    )
                }
            }
            form.value = updated
        }
    }

    val uiState: StateFlow<TourEditUiState> = form.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        TourEditUiState(tourId = route.tourId, isNew = route.tourId == 0L),
    )

    fun onNameChanged(value: String) {
        form.value = form.value.copy(name = value)
    }

    fun onDescriptionChanged(value: String) {
        form.value = form.value.copy(description = value)
    }

    fun onDepartureChanged(value: String) {
        form.value = form.value.copy(departureText = value)
    }

    fun onNightsChanged(value: String) {
        if (value.length <= 2 && value.all(Char::isDigit)) {
            form.value = form.value.copy(nightsText = value)
        }
    }

    fun onPriceChanged(value: String) {
        if (value.length <= 9 && (value.all(Char::isDigit) || value.isEmpty())) {
            form.value = form.value.copy(priceText = value)
        }
    }

    fun onQuotaChanged(value: String) {
        if (value.length <= 3 && (value.all(Char::isDigit) || value.isEmpty())) {
            form.value = form.value.copy(quotaText = value)
        }
    }

    fun onOperatorSelected(item: TourOperatorEntity?) {
        form.value = form.value.copy(operator = item)
    }

    fun onDestinationSelected(item: DestinationEntity) {
        // Смена направления сбрасывает отель — отели привязаны к направлениям.
        form.value = form.value.copy(destination = item, hotel = null)
    }

    fun onHotelSelected(item: HotelEntity) {
        form.value = form.value.copy(hotel = item)
    }

    fun onMealSelected(item: MealTypeEntity) {
        form.value = form.value.copy(mealType = item)
    }

    fun save() {
        val current = form.value
        val departure = parseDate(current.departureText)
        val nights = current.nightsText.toIntOrNull() ?: 0
        val price = parseRubles(current.priceText)
        val quota = current.quotaText.toIntOrNull() ?: 0
        val errors = buildList {
            if (current.name.isBlank()) add(TourFieldError.NAME)
            if (current.destination == null || current.hotel == null || current.mealType == null) {
                add(TourFieldError.PICKERS)
            }
            if (departure == null || departure <= System.currentTimeMillis()) add(TourFieldError.DEPARTURE)
            if (nights !in 1..30) add(TourFieldError.NIGHTS)
            if (price == null) add(TourFieldError.PRICE)
            if (quota !in 1..100) add(TourFieldError.QUOTA)
        }
        if (errors.isNotEmpty()) {
            form.value = current.copy(errors = errors)
            return
        }
        viewModelScope.launch {
            val old = if (route.tourId == 0L) null else container.toursRepository.findTour(route.tourId)
            container.toursRepository.save(
                TourEntity(
                    id = route.tourId,
                    name = current.name.trim(),
                    operatorId = current.operator?.id,
                    destinationId = current.destination!!.id,
                    hotelId = current.hotel!!.id,
                    mealTypeId = current.mealType!!.id,
                    departureDate = departure!!,
                    nights = nights,
                    priceMinor = price!!,
                    quota = quota,
                    description = current.description.trim(),
                    status = old?.status ?: TourStatus.ACTIVE,
                ),
            )
            form.value = form.value.copy(saved = true)
        }
    }
}
