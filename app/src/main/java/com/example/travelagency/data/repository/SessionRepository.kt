package com.example.travelagency.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.travelagency.core.sha256
import com.example.travelagency.data.dao.EmployeesDao
import com.example.travelagency.data.entity.EmployeeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

private val Context.sessionStore by preferencesDataStore(name = "session")

/** Сессия сотрудника: проверка пароля, текущий пользователь, последний вошедший (DataStore, лекция 12). */
class SessionRepository(
    private val context: Context,
    private val employeesDao: EmployeesDao,
) {
    private val _currentEmployee = MutableStateFlow<EmployeeEntity?>(null)
    val currentEmployee: MutableStateFlow<EmployeeEntity?> = _currentEmployee

    fun observeEmployees(): Flow<List<EmployeeEntity>> = employeesDao.observeAll()

    val lastEmployeeId: Flow<Long> = context.sessionStore.data.map { prefs ->
        prefs[LAST_EMPLOYEE_ID] ?: 0L
    }

    /** Вход: сотрудник + пароль. Пароли хранятся в БД хешем SHA-256. */
    suspend fun login(employeeId: Long, password: String): Boolean {
        val employee = employeesDao.findById(employeeId) ?: return false
        if (employee.passwordHash != password.sha256()) return false
        _currentEmployee.value = employee
        context.sessionStore.edit { prefs ->
            prefs[LAST_EMPLOYEE_ID] = employeeId
        }
        return true
    }

    fun logout() {
        _currentEmployee.value = null
    }

    private companion object {
        val LAST_EMPLOYEE_ID = longPreferencesKey("last_employee_id")
    }
}
