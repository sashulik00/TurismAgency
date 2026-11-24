package com.example.travelagency.ui.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.travelagency.R
import com.example.travelagency.TravelAgencyApplication
import com.example.travelagency.data.entity.EmployeeRole
import com.example.travelagency.ui.components.TabTopAppBar
import com.example.travelagency.ui.theme.NavyText
import com.example.travelagency.ui.theme.WhiteText

@Composable
internal fun MenuScreen(
    employeeRole: EmployeeRole?,
    onOpenScreen: (Any) -> Unit,
    onLogout: () -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val container = (context.applicationContext as TravelAgencyApplication).container
    val employee by container.sessionRepository.currentEmployee.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TabTopAppBar(R.string.menu_title) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        employee?.fullName ?: "",
                        color = WhiteText,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        (employee?.position ?: "") + " · " +
                            stringResource(
                                if (employee?.role == EmployeeRole.ADMIN) {
                                    R.string.menu_role_admin
                                } else {
                                    R.string.menu_role_manager
                                },
                            ),
                        color = WhiteText,
                    )
                }
            }

            if (employeeRole == EmployeeRole.ADMIN) {
                Text(
                    stringResource(R.string.menu_admin_section),
                    fontWeight = FontWeight.Bold,
                    color = NavyText,
                )
                MenuRow(Icons.Filled.Apartment, stringResource(R.string.menu_operators)) {
                    onOpenScreen(com.example.travelagency.ui.navigation.OperatorsRoute)
                }
                MenuRow(Icons.Filled.Flight, stringResource(R.string.menu_destinations)) {
                    onOpenScreen(com.example.travelagency.ui.navigation.DestinationsRoute)
                }
                MenuRow(Icons.Filled.BeachAccess, stringResource(R.string.menu_hotels)) {
                    onOpenScreen(com.example.travelagency.ui.navigation.HotelsRoute)
                }
                MenuRow(Icons.Filled.RestaurantMenu, stringResource(R.string.menu_meal_types)) {
                    onOpenScreen(com.example.travelagency.ui.navigation.MealTypesRoute)
                }
                MenuRow(Icons.Filled.Person, stringResource(R.string.menu_employees)) {
                    onOpenScreen(com.example.travelagency.ui.navigation.EmployeesRoute)
                }
                MenuRow(Icons.Filled.History, stringResource(R.string.menu_audit)) {
                    onOpenScreen(com.example.travelagency.ui.navigation.AuditRoute)
                }
                Spacer(Modifier.height(6.dp))
            }

            MenuRow(Icons.Filled.Settings, stringResource(R.string.menu_settings)) {
                onOpenScreen(com.example.travelagency.ui.navigation.SettingsRoute)
            }
            Button(
                onClick = {
                    container.sessionRepository.logout()
                    onLogout()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.action_logout), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MenuRow(icon: ImageVector, title: String, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp),
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Text(title, color = NavyText, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
