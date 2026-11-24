package com.example.travelagency.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.travelagency.R
import com.example.travelagency.data.entity.EmployeeRole
import com.example.travelagency.ui.admin.AuditScreen
import com.example.travelagency.ui.admin.DestinationsScreen
import com.example.travelagency.ui.admin.EmployeesScreen
import com.example.travelagency.ui.admin.HotelsScreen
import com.example.travelagency.ui.admin.MealTypesScreen
import com.example.travelagency.ui.admin.OperatorsScreen
import com.example.travelagency.ui.bookings.BookingsScreen
import com.example.travelagency.ui.clients.ClientDetailsScreen
import com.example.travelagency.ui.clients.ClientEditScreen
import com.example.travelagency.ui.clients.ClientsScreen
import com.example.travelagency.ui.contracts.ContractDetailsScreen
import com.example.travelagency.ui.contracts.ContractsScreen
import com.example.travelagency.ui.contracts.PaymentEditScreen
import com.example.travelagency.ui.login.LoginScreen
import com.example.travelagency.ui.menu.MenuScreen
import com.example.travelagency.ui.settings.SettingsScreen
import com.example.travelagency.ui.theme.TranslucentBar
import com.example.travelagency.ui.theme.WhiteText
import com.example.travelagency.ui.tours.TourDetailsScreen
import com.example.travelagency.ui.tours.TourEditScreen
import com.example.travelagency.ui.tours.ToursScreen
import com.example.travelagency.ui.tours.BookingEditScreen
import kotlin.reflect.KClass

private data class BottomTab(val route: Any, val routeClass: KClass<*>, val icon: ImageVector, val labelRes: Int)

private val bottomTabs = listOf(
    BottomTab(ToursTab, ToursTab::class, Icons.Filled.TravelExplore, R.string.tab_tours),
    BottomTab(ClientsTab, ClientsTab::class, Icons.Filled.People, R.string.tab_clients),
    BottomTab(BookingsTab, BookingsTab::class, Icons.Filled.Luggage, R.string.tab_bookings),
    BottomTab(ContractsTab, ContractsTab::class, Icons.Filled.Description, R.string.tab_contracts),
    BottomTab(MenuTab, MenuTab::class, Icons.Filled.Menu, R.string.tab_menu),
)

/** Корень приложения: сессия сотрудника + нижняя панель + NavHost (лекция 09). */
@Composable
internal fun AppRoot() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val container = (context.applicationContext as com.example.travelagency.TravelAgencyApplication).container
    val employee by container.sessionRepository.currentEmployee.collectAsState()
    AppRootContent(employeeRole = employee?.role)
}

@Composable
private fun AppRootContent(employeeRole: EmployeeRole?) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val showBottomBar = bottomTabs.any { currentDestination.matchesTab(it.routeClass) }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = TranslucentBar) {
                    bottomTabs.forEach { tab ->
                        val selected = currentDestination.matchesTab(tab.routeClass)
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = stringResource(tab.labelRes)) },
                            label = { Text(stringResource(tab.labelRes)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = WhiteText,
                                selectedTextColor = WhiteText,
                                unselectedIconColor = WhiteText.copy(alpha = 0.7f),
                                unselectedTextColor = WhiteText.copy(alpha = 0.7f),
                                indicatorColor = WhiteText.copy(alpha = 0.25f),
                            ),
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = LoginRoute,
            modifier = Modifier.padding(padding),
        ) {
            composable<LoginRoute> {
                LoginScreen(
                    onLoggedIn = {
                        navController.navigate(ToursTab) {
                            popUpTo(LoginRoute) { inclusive = true }
                        }
                    },
                )
            }
            composable<ToursTab> {
                ToursScreen(
                    onOpenTour = { tourId -> navController.navigate(TourDetailsRoute(tourId)) },
                    onAddTour = { navController.navigate(TourEditRoute()) },
                )
            }
            composable<ClientsTab> {
                ClientsScreen(
                    onOpenClient = { clientId -> navController.navigate(ClientDetailsRoute(clientId)) },
                    onAddClient = { navController.navigate(ClientEditRoute()) },
                )
            }
            composable<BookingsTab> {
                BookingsScreen(
                    onOpenBooking = { bookingId ->
                        navController.navigate(BookingEditRoute(bookingId = bookingId))
                    },
                    onAddBooking = { navController.navigate(BookingEditRoute()) },
                )
            }
            composable<ContractsTab> {
                ContractsScreen(
                    onOpenContract = { contractId -> navController.navigate(ContractDetailsRoute(contractId)) },
                )
            }
            composable<MenuTab> {
                MenuScreen(
                    employeeRole = employeeRole,
                    onOpenScreen = { route -> navController.navigate(route) },
                    onLogout = {
                        navController.navigate(LoginRoute) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                )
            }
            composable<TourDetailsRoute> { entry ->
                val route = entry.toRoute<TourDetailsRoute>()
                TourDetailsScreen(
                    tourId = route.tourId,
                    onBook = { tourId -> navController.navigate(BookingEditRoute(tourId = tourId)) },
                    onEditTour = { navController.navigate(TourEditRoute(route.tourId)) },
                    onBack = { navController.navigateUp() },
                )
            }
            composable<TourEditRoute> { TourEditScreen(onDone = { navController.navigateUp() }) }
            composable<BookingEditRoute> { entry ->
                val route = entry.toRoute<BookingEditRoute>()
                BookingEditScreen(
                    bookingId = route.bookingId,
                    presetTourId = route.tourId,
                    onDone = { navController.navigateUp() },
                )
            }
            composable<ClientDetailsRoute> { entry ->
                val route = entry.toRoute<ClientDetailsRoute>()
                ClientDetailsScreen(
                    clientId = route.clientId,
                    onEdit = { navController.navigate(ClientEditRoute(route.clientId)) },
                    onBack = { navController.navigateUp() },
                )
            }
            composable<ClientEditRoute> { entry ->
                val route = entry.toRoute<ClientEditRoute>()
                ClientEditScreen(clientId = route.clientId, onDone = { navController.navigateUp() })
            }
            composable<ContractDetailsRoute> { entry ->
                val route = entry.toRoute<ContractDetailsRoute>()
                ContractDetailsScreen(
                    contractId = route.contractId,
                    onAddPayment = { navController.navigate(PaymentEditRoute(route.contractId)) },
                    onBack = { navController.navigateUp() },
                )
            }
            composable<PaymentEditRoute> { entry ->
                val route = entry.toRoute<PaymentEditRoute>()
                PaymentEditScreen(contractId = route.contractId, onDone = { navController.navigateUp() })
            }
            composable<SettingsRoute> { SettingsScreen(onBack = { navController.navigateUp() }) }
            composable<OperatorsRoute> { OperatorsScreen(onBack = { navController.navigateUp() }) }
            composable<DestinationsRoute> { DestinationsScreen(onBack = { navController.navigateUp() }) }
            composable<HotelsRoute> { HotelsScreen(onBack = { navController.navigateUp() }) }
            composable<MealTypesRoute> { MealTypesScreen(onBack = { navController.navigateUp() }) }
            composable<EmployeesRoute> { EmployeesScreen(onBack = { navController.navigateUp() }) }
            composable<AuditRoute> { AuditScreen(onBack = { navController.navigateUp() }) }
        }
    }
}

private fun NavDestination?.matchesTab(routeClass: KClass<*>): Boolean =
    this?.hasRoute(routeClass) == true
