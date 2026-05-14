package com.transit.gramayatri.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.transit.gramayatri.Destinations
import com.transit.gramayatri.data.repository.busRepo
import com.transit.gramayatri.data.repository.userRepo
import com.transit.gramayatri.ui.ping.pingScreen
import com.transit.gramayatri.ui.profile.editProfileScreen
import com.transit.gramayatri.ui.profile.profileScreen
import com.transit.gramayatri.ui.profile.SavedPreferencesScreen
import com.transit.gramayatri.ui.profile.SettingsScreen
import com.transit.gramayatri.ui.search.busTimelineScreen
import com.transit.gramayatri.ui.search.searchScreen
import com.transit.gramayatri.ui.theme.BrandOrange
import com.transit.gramayatri.ui.theme.TextSecondary

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

private val bottomNavItems = listOf(
    BottomNavItem("Ping",    Icons.Default.Campaign, "ping_tab"),
    BottomNavItem("Search",  Icons.Default.Search,   "search_tab"),
    BottomNavItem("Profile", Icons.Default.Person,   "profile_tab")
)

@Composable
fun homeScreen(
    userRepository: userRepo,
    busRepository: busRepo,
    onLogOut: () -> Unit
) {

    val homeNavController = rememberNavController()
    var selectedTab by remember { mutableStateOf("search_tab") }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = androidx.compose.ui.graphics.Color.White,
                tonalElevation = androidx.compose.ui.unit.Dp(4f)
            ) {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = selectedTab == item.route,
                        onClick  = {
                            selectedTab = item.route
                            homeNavController.navigate(item.route) {
                                popUpTo(homeNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        },
                        icon  = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (selectedTab == item.route) BrandOrange
                                else TextSecondary
                            )
                        },
                        label = {
                            Text(
                                text  = item.label,
                                color = if (selectedTab == item.route) BrandOrange
                                else TextSecondary
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = androidx.compose.ui.graphics.Color.Transparent
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = homeNavController,
            startDestination = "search_tab",
            modifier         = Modifier.padding(innerPadding)
        ) {
            // ── Search tab ─────────────────────────────────────────────────
            composable("search_tab") {
                searchScreen(
                    busRepository = busRepository,
                    onBusSelected = { busId ->
                        homeNavController.navigate(Destinations.busTimeline(busId))
                    }
                )
            }

            // ── Bus Timeline (opened from Search) ──────────────────────────
            composable(Destinations.BUS_TIMELINE) { backStackEntry ->
                val busId = backStackEntry.arguments?.getString("busId") ?: ""
                busTimelineScreen(
                    busId         = busId,
                    busRepository = busRepository,
                    onBack        = { homeNavController.popBackStack() }
                )
            }

            // ── Ping tab ───────────────────────────────────────────────────
            composable("ping_tab") {
                pingScreen(
                    userRepository = userRepository,
                    busRepository  = busRepository
                )
            }

            // ── Profile tab ────────────────────────────────────────────────
            composable("profile_tab") {
                profileScreen(
                    userRepository = userRepository,
                    onEditProfile  = { homeNavController.navigate(Destinations.EDIT_PROFILE) },
                    onSavedPrefs   = { homeNavController.navigate(Destinations.SAVED_PREFS) },
                    onSettings     = { homeNavController.navigate(Destinations.SETTINGS) },
                    onLogOut       = onLogOut
                )
            }

            composable(Destinations.EDIT_PROFILE) {
                editProfileScreen(
                    userRepository = userRepository,
                    onBack         = { homeNavController.popBackStack() }
                )
            }

            composable(Destinations.SAVED_PREFS) {
                SavedPreferencesScreen(
                    onBack = { homeNavController.popBackStack() }
                )
            }

            composable(Destinations.SETTINGS) {
                SettingsScreen(
                    onBack = { homeNavController.popBackStack() }
                )
            }
        }
    }
}