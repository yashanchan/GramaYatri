package com.transit.gramayatri

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.transit.gramayatri.data.local.appDatabase
import com.transit.gramayatri.data.repository.busRepo
import com.transit.gramayatri.data.repository.userRepo
import com.transit.gramayatri.ui.account.accountCreationScreen
import com.transit.gramayatri.ui.home.homeScreen
import com.transit.gramayatri.ui.theme.GramaYatriTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GramaYatriTheme {

                val userRepo = remember { userRepo(applicationContext) }
                val busRepo  = remember {
                    busRepo(appDatabase.getDatabase(applicationContext))
                }
                val navController = rememberNavController()

                val startDestination = if (userRepo.isProfileSaved()) {
                    Destinations.HOME
                } else {
                    Destinations.ACCOUNT_CREATION
                }

                NavHost(
                    navController    = navController,
                    startDestination = startDestination
                ) {
                    composable(Destinations.ACCOUNT_CREATION) {
                        accountCreationScreen(
                            userRepository = userRepo,
                            onProfileSaved = {
                                // Go to Home and remove Account Creation from back stack
                                navController.navigate(Destinations.HOME) {
                                    popUpTo(Destinations.ACCOUNT_CREATION) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Destinations.HOME) {
                        homeScreen(
                            userRepository = userRepo,
                            busRepository  = busRepo,
                            onLogOut = {
                                userRepo.clearProfile()
                                navController.navigate(Destinations.ACCOUNT_CREATION) {
                                    popUpTo(Destinations.HOME) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}