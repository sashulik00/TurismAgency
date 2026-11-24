package com.example.travelagency.ui.tours

import android.app.Application
import android.database.sqlite.SQLiteException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.travelagency.R
import com.example.travelagency.TravelAgencyApplication
import com.example.travelagency.core.bookingTotal
import com.example.travelagency.data.dao.TourListItem
import com.example.travelagency.data.entity.BookingEntity
import com.example.travelagency.data.entity.BookingStatus
import com.example.travelagency.data.entity.ClientEntity
import com.example.travelagency.ui.navigation.BookingEditRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Ошибки формы — какие поля подсветить (валидация по лекции 04). */
enum class BookingFieldError { CLIENT, TOUR, TOURISTS }

data class BookingEditUiState(
    val bookingId: Long = 0,
    val isNew: Boolean = true,
    val clients: List<ClientEntity> = emptyList(),
    val availableTours: List<TourListItem> = emptyList(),
    val client: ClientEntity? = null,
    val tour: TourListItem? = null,
    val tourFixed: Boolean = false,
    val tourists: String = "1",
    val status: BookingStatus = BookingStatus.PENDING,
    val withPrepayment: Boolean = true,
    val totalMinor: Long = 0,
    val fieldErrors: List<BookingFieldError> = emptyList(),
    val errorMessageRes: Int? = null,
    val saved: Boolean = false,
)

private data class BookingForm(
    val client: ClientEntity? = null,
    val tourists: String = "1",
    val status: BookingStatus = BookingStatus.PENDING,
    val withPrepayment: Boolean = true,
    val fieldErrors: List<BookingFieldError> = emptyList(),
    val errorMessageRes: Int? = null,
    val saved: Boolean = false,
)

class BookingEditViewModel(application: Application, savedStateHandle: SavedStateHandle) :
    AndroidViewModel(application) {
    private val container = (application as TravelAgencyApplication).container
    private val route = savedStateHandle.toRoute<BookingEditRoute>()

    private val form = MutableStateFlow(BookingForm())
    private val booking = MutableStateFlow<BookingEntity?>(null)
    private val selectedTour = MutableStateFlow<TourListItem?>(null)

    init {
        if (route.bookingId != 0L) {
            viewModelScope.launch {
                val existing = container.bookingsRepository.findBooking(route.bookingId)
                booking.value = existing
                if (existing != null) {
                    form.value = form.value.copy(
                        client = container.clientsRepository.findClient(existing.clientId),
                        tourists = existing.touristsCount.toString(),
                        status = existing.status,
                        withPrepayment = false,
                    )
                    container.toursRepository.observeTourDetails(existing.tourId).first()?.let { details ->
                        selectedTour.value = TourListItem(
                            id = details.id,
                            name = details.name,
                            country = details.country,
                            city = details.city,
                            hotelName = details.hotelName,
                            stars = details.stars,
                            mealName = details.mealName,
                            operatorName = details.operatorName,
                            departureDate = details.departureDate,
                            nights = details.nights,
                            priceMinor = details.priceMinor,
                            quota = details.quota,
                            status = details.status,
                        )
                    }
                }
            }
        } else if (route.tourId != 0L) {
            viewModelScope.launch {
                container.toursRepository.observeTourDetails(route.tourId).first()?.let { details ->
                    selectedTour.value = TourListItem(
                        id = details.id,
                        name = details.name,
                        country = details.country,
                        city = details.city,
                        hotelName = details.hotelName,
                        stars = details.stars,
                        mealName = details.mealName,
                        operatorName = details.operatorName,
                        departureDate = details.departureDate,
                        nights = details.nights,
                        priceMinor = details.priceMinor,
                        quota = details.quota,
                        status = details.status,
                    )
                }
            }
        }
    }

    val uiState: StateFlow<BookingEditUiState> = combine(
        container.clientsRepository.observeClients(""),
        container.toursRepository.observeTours("", onlyActive = true, destinationId = 0),
        booking,
        selectedTour,
        form,
    ) { clients, tours, bookingEntity, tour, currentForm ->
        BookingEditUiState(
            bookingId = route.bookingId,
            isNew = route.bookingId == 0L,
            clients = clients,
            availableTours = tours,
            client = currentForm.client,
            tour = tour,
            tourFixed = route.tourId != 0L || route.bookingId != 0L,
            tourists = currentForm.tourists,
            status = currentForm.status,
            withPrepayment = currentForm.withPrepayment,
            totalMinor = tour?.let { bookingTotal(it.priceMinor, currentForm.tourists.toIntOrNull() ?: 1) } ?: 0,
            fieldErrors = currentForm.fieldErrors,
            errorMessageRes = currentForm.errorMessageRes,
            saved = currentForm.saved,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BookingEditUiState())

    fun onClientSelected(client: ClientEntity) {
        form.value = form.value.copy(client = client, fieldErrors = form.value.fieldErrors - BookingFieldError.CLIENT)
    }

    fun onTourSelected(tour: TourListItem) {
        selectedTour.value = tour
        form.value = form.value.copy(fieldErrors = form.value.fieldErrors - BookingFieldError.TOUR)
    }

    fun onTouristsChanged(value: String) {
        if (value.length <= 2 && value.all(Char::isDigit)) {
            form.value = form.value.copy(tourists = value)
        }
    }

    fun onStatusSelected(status: BookingStatus) {
        form.value = form.value.copy(status = status)
    }

    fun onPrepaymentChanged(value: Boolean) {
        form.value = form.value.copy(withPrepayment = value)
    }

    fun save() {
        val currentForm = form.value
        val tour = selectedTour.value
        val tourists = currentForm.tourists.toIntOrNull() ?: 0
        val errors = buildList {
            if (currentForm.client == null) add(BookingFieldError.CLIENT)
            if (tour == null) add(BookingFieldError.TOUR)
            if (tourists !in 1..10) add(BookingFieldError.TOURISTS)
        }
        if (errors.isNotEmpty()) {
            form.value = currentForm.copy(fieldErrors = errors)
            return
        }
        viewModelScope.launch {
            try {
                if (route.bookingId != 0L) {
                    container.bookingsRepository.updateStatus(route.bookingId, currentForm.status)
                } else {
                    val tourEntity = container.toursRepository.findTour(tour!!.id)
                    if (tourEntity != null) {
                        container.bookingsRepository.createBooking(
                            clientId = currentForm.client!!.id,
                            tour = tourEntity,
                            touristsCount = tourists,
                            withPrepayment = currentForm.withPrepayment,
                        )
                    }
                }
                form.value = form.value.copy(saved = true)
            } catch (e: SQLiteException) {
                form.value = form.value.copy(
                    errorMessageRes = if (e.message?.contains("QUOTA") == true) {
                        R.string.error_quota
                    } else {
                        R.string.error_unknown
                    },
                )
            }
        }
    }
}
