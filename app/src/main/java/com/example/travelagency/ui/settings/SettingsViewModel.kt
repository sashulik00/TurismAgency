package com.example.travelagency.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelagency.TravelAgencyApplication
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val hideAmounts: Boolean = false,
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val container = (application as TravelAgencyApplication).container

    val uiState: StateFlow<SettingsUiState> = container.settingsRepository.hideAmounts
        .map { SettingsUiState(hideAmounts = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun setHideAmounts(value: Boolean) {
        viewModelScope.launch { container.settingsRepository.setHideAmounts(value) }
    }
}
