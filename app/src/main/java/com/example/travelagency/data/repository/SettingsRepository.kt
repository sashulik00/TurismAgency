package com.example.travelagency.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsStore by preferencesDataStore(name = "settings")

/** Настройки приложения в Preferences DataStore — тема лекции 12. */
class SettingsRepository(private val context: Context) {
    private val hideAmountsKey = booleanPreferencesKey("hide_amounts")

    val hideAmounts: Flow<Boolean> = context.settingsStore.data.map { prefs ->
        prefs[hideAmountsKey] ?: false
    }

    suspend fun setHideAmounts(value: Boolean) {
        context.settingsStore.edit { prefs ->
            prefs[hideAmountsKey] = value
        }
    }
}
