package com.example.travelagency.data.repository

import android.content.Context
import com.example.travelagency.data.db.DatabaseProvider

/** Ручной контейнер зависимостей: зависимости собираются в одном месте, а не внутри экранов (лекция 14). */
class AppContainer(context: Context) {
    private val database = DatabaseProvider.get(context)

    val toursRepository = ToursRepository(
        toursDao = database.toursDao(),
        destinationsDao = database.destinationsDao(),
    )
    val clientsRepository = ClientsRepository(database.clientsDao())
    val bookingsRepository = BookingsRepository(database.bookingsDao())
    val directoryRepository = DirectoryRepository(
        operatorsDao = database.operatorsDao(),
        destinationsDao = database.destinationsDao(),
        hotelsDao = database.hotelsDao(),
        mealTypesDao = database.mealTypesDao(),
        employeesDao = database.employeesDao(),
        auditDao = database.auditDao(),
    )
    val paymentsRepository = PaymentsRepository(database.paymentsDao())
    val contractsRepository = ContractsRepository(database.contractsDao())
    val sessionRepository = SessionRepository(
        context = context,
        employeesDao = database.employeesDao(),
    )
    val settingsRepository = SettingsRepository(context)
}
