package com.application.presence.Navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.application.presence.Screen.AddEventScreenUI
import com.application.presence.Screen.AuthDetailScreen
import com.application.presence.Screen.AuthScreen
import com.application.presence.Screen.Components.ScannerViewModelFactory
import com.application.presence.Screen.HomeScreen
import com.application.presence.Screen.ScannerScreen
import com.application.presence.Screen.SplashScreen
import com.application.presence.data.local.DeviceManager
import com.application.presence.repository.AuthRepository
import com.application.presence.repository.HomeRepository
import com.application.presence.repository.LocalUserRepository
import com.application.presence.viewmodel.AuthViewModel
import com.application.presence.viewmodel.EventViewModel
import com.application.presence.viewmodel.Factory.AuthViewModelFactory
import com.application.presence.viewmodel.Factory.EventViewModelFactory
import com.application.presence.viewmodel.Factory.SplashViewModelFactory
import com.application.presence.viewmodel.ScannerViewModel
import com.application.presence.viewmodel.SplashViewModel
import kotlinx.serialization.Serializable

@Composable
fun NavGraph(){
    val navController = rememberNavController()
    val context = LocalContext.current

    val authRepository = remember(context) { AuthRepository(context) }
    val localUserRepository = remember(context) { LocalUserRepository(context) }
    val deviceManager = remember(context) { DeviceManager(context) }

    val splashFactory = remember(localUserRepository) {
        SplashViewModelFactory(localUserRepository)
    }

    val authFactory = remember(authRepository, deviceManager, localUserRepository) {
        AuthViewModelFactory(
            repository = authRepository,
            deviceManager = deviceManager,
            localUserRepository = localUserRepository,
            context
        )
    }
    NavHost(navController = navController, startDestination = splashScreen){
        composable<splashScreen> {
            val splashViewModel: SplashViewModel = viewModel(factory = splashFactory)
            SplashScreen(
                viewModel = splashViewModel,
                onNavigateToLogin = {
                    navController.navigate(authScreen){
                        popUpTo(splashScreen){inclusive = true}
                    }
                },
                onNavigateToHome = {
                    navController.navigate(homeScreen){
                        popUpTo(authScreen){inclusive = true}
                    }
                },
                onNavigateToDetail = {
                    navController.navigate(authdetail)
                }
            )
        }

        composable<authScreen> {
            val authViewModel: AuthViewModel = viewModel(factory = authFactory)
            AuthScreen(
                authViewModel,
                onAuthSuccess = {
                    navController.navigate(authdetail){
                        popUpTo(authScreen){inclusive = false}
                    }
                }

            ){
                navController.navigate(homeScreen){
                    popUpTo(authScreen){inclusive = true}
                }
            }
        }

        composable<authdetail> {
            val authViewModel : AuthViewModel = viewModel(factory = authFactory)
            AuthDetailScreen(
                viewModel = authViewModel,
                context = context,
                repo = authRepository,
                onSaveClick = {
                    navController.navigate(homeScreen){
                        popUpTo(authdetail){inclusive = true}
                    }
                }
            )
        }

        composable<homeScreen> {
            val repository = HomeRepository()
            val factory = EventViewModelFactory(repository)
            val eventViewModel: EventViewModel = viewModel(factory = factory)
            HomeScreen(
                viewmodel = eventViewModel,
                onScannerClick = {navController.navigate(scannerScreen)},
                {navController.navigate(addEventScreen)},
                {}
                )
        }

        composable<scannerScreen> {
            val viewmodel: ScannerViewModel = viewModel(
                factory = ScannerViewModelFactory(
                    onSubmissionSuccess = {
                        navController.navigate(homeScreen){
                            popUpTo(homeScreen){inclusive = false}
                            Toast.makeText(
                                context,
                                "Successfully Submitted Attendance",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
            )
            ScannerScreen(viewmodel)
        }

        composable<addEventScreen> {
            AddEventScreenUI({navController.navigate((homeScreen))})
        }
    }
}


@Serializable
object authScreen

@Serializable
object splashScreen

@Serializable
object authdetail

@Serializable
object homeScreen

@Serializable
object scannerScreen

@Serializable
object addEventScreen