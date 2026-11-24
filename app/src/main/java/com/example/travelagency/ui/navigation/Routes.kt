package com.example.travelagency.ui.navigation

import kotlinx.serialization.Serializable

/** Типизированные маршруты @Serializable — по лекции 09 и практике 09. */

@Serializable
data object LoginRoute

// Вкладки нижней панели.
@Serializable
data object ToursTab

@Serializable
data object ClientsTab

@Serializable
data object BookingsTab

@Serializable
data object ContractsTab

@Serializable
data object MenuTab

// Экраны деталей и форм — в стек поверх вкладок.
@Serializable
data class TourDetailsRoute(val tourId: Long)

@Serializable
data class TourEditRoute(val tourId: Long = 0)

@Serializable
data class BookingEditRoute(val bookingId: Long = 0, val tourId: Long = 0)

@Serializable
data class ClientDetailsRoute(val clientId: Long)

@Serializable
data class ClientEditRoute(val clientId: Long = 0)

@Serializable
data class ContractDetailsRoute(val contractId: Long)

@Serializable
data class PaymentEditRoute(val contractId: Long)

@Serializable
data object SettingsRoute

// Раздел администратора.
@Serializable
data object OperatorsRoute

@Serializable
data object DestinationsRoute

@Serializable
data object HotelsRoute

@Serializable
data object MealTypesRoute

@Serializable
data object EmployeesRoute

@Serializable
data object AuditRoute
