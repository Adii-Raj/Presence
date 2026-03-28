package com.application.presence.Navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.application.presence.Screen.AddEventScreenUI
import com.application.presence.Screen.AttendanceScreen
import com.application.presence.Screen.AuthDetailScreen
import com.application.presence.Screen.AuthScreen
import com.application.presence.Screen.Components.ScannerViewModelFactory
import com.application.presence.Screen.HomeScreen
import com.application.presence.Screen.QrGeneratorScreen
import com.application.presence.Screen.ScannerScreen
import com.application.presence.Screen.SplashScreen
import com.application.presence.data.local.DeviceManager
import com.application.presence.data.model.StudentAttendance
import com.application.presence.repository.AuthRepository
import com.application.presence.repository.HomeRepository
import com.application.presence.repository.LocalUserRepository
import com.application.presence.viewmodel.AddEventViewModel
import com.application.presence.viewmodel.AttendanceScreenViewModel
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
            val authViewModel: AuthViewModel = viewModel(factory = authFactory)
            val repository = HomeRepository()
            val factory = EventViewModelFactory(repository)
            val eventViewModel: EventViewModel = viewModel(factory = factory)
            HomeScreen(
                authViewModel = authViewModel,
                eventViewmodel = eventViewModel,
                onScannerClick = {navController.navigate(scannerScreen)},
                {navController.navigate(addEventScreen)},
                {navController.navigate(attendanceScreen)},
                {navController.navigate(qrGeneratorScreen)}
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
            val viewModel: AddEventViewModel = viewModel()
            AddEventScreenUI(onSaveClick = {event ->
                viewModel.insertProfile(event)
            })
        }

        composable<attendanceScreen> {
            // 1. Dummy Data
            val dummyEventsList = listOf(
                "Tech Symposium 2026",
                "Android Dev Workshop",
                "Universal Human Values Exam"
            )
            val dummyStudentsList = listOf(
                StudentAttendance("id_1", "Aditya Sharma", "CS-2024-001", "9876543210", "A", "4th"),
                StudentAttendance("id_2", "Priya Singh", "CS-2024-015", "9876543211", "B", "4th"),
                StudentAttendance("id_3", "Rohan Verma", "EC-2024-042", "9876543212", "A", "4th"),
                StudentAttendance("id_4", "Sneha Kapoor", "ME-2024-008", "9876543213", "C", "4th"),
                StudentAttendance("id_5", "Amit Kumar", "CS-2024-055", "9876543214", "A", "4th"),
                StudentAttendance("id_6", "Neha Gupta", "IT-2024-022", "9876543215", "B", "4th")
            )

            // 2. ViewModel & State
            val viewModel: AttendanceScreenViewModel = viewModel()
            val selectedIds by viewModel.selectedStudentIds.collectAsStateWithLifecycle()

            // ADDED: Local state to hold the currently selected event so the dropdown works
            var currentSelectedEvent by remember { mutableStateOf(dummyEventsList[0]) }

            // 3. The Screen Call (Using Named Arguments)
            AttendanceScreen(
                eventsList = dummyEventsList,
                selectedEvent = currentSelectedEvent,
                studentList = dummyStudentsList,
                selectedStudentIds = selectedIds,
                onEventSelected = { eventName ->
                    currentSelectedEvent = eventName
                },
                onStudentSelectionChange = { studentId, isChecked ->
                    // Route the checkbox clicks to your ViewModel!
                    viewModel.toggleStudentSelection(studentId, isChecked)
                },
                onClearSelection = {},
                onDeleteSelectedClick = {
                    println("Dummy Delete Triggered for: $selectedIds")
                },
                onDownloadExcelClick = {
                    println("Dummy Download Triggered")
                }
            )
        }

        composable<qrGeneratorScreen> {
            QrGeneratorScreen()
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

@Serializable
object attendanceScreen

@Serializable
object qrGeneratorScreen