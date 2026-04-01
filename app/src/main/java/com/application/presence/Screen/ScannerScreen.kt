package com.application.presence.Screen

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.application.presence.R
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.application.presence.Screen.Components.CameraScanner
import com.application.presence.Screen.Components.CustomDropdown
import com.application.presence.Screen.Components.RequestCameraPermission
import com.application.presence.data.model.LocationState
import com.application.presence.data.state.ScannerSubmissionState
import com.application.presence.viewmodel.ScannerViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel,
    onPopBackStack:() -> Unit
) {
    val scannerState by viewModel.scannerState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var selectedSem by remember { mutableStateOf("") }
    var selectedSec by remember { mutableStateOf("") }

    val currentLocation by viewModel.locationState.collectAsStateWithLifecycle()
    val submissionState by viewModel.submissionState.collectAsStateWithLifecycle()
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()

    LaunchedEffect(submissionState) {
        when (val state = submissionState) {

            is ScannerSubmissionState.Success -> {
                Toast.makeText(context, "Attendance Marked Successfully", Toast.LENGTH_LONG).show()

                viewModel.changeSubmissionState(ScannerSubmissionState.Stopped)
                onPopBackStack()
            }

            is ScannerSubmissionState.Error -> {
                Toast.makeText(context, "Error: ${state.message}", Toast.LENGTH_LONG).show()

                viewModel.changeSubmissionState(ScannerSubmissionState.Stopped)

                viewModel.restartScanning()
                onPopBackStack()
            }

            else -> { /* Do nothing */ }
        }
    }


    val settingResultRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            // User clicked "OK" to turn on GPS!
            viewModel.startTracking()
        } else {
            // User clicked "No Thanks"
            viewModel.setLocationError("Device location must be turned on to verify attendance.")
        }
    }

    // A helper function to check hardware settings BEFORE tracking
    val checkDeviceLocationSettings = {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000).build()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(context)

        client.checkLocationSettings(builder.build()).apply {
            addOnSuccessListener {
                // GPS is already ON. We are good to go!
                viewModel.startTracking()
            }
            addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    // GPS is OFF, but Android can show a popup to fix it.
                    try {
                        val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                        settingResultRequest.launch(intentSenderRequest)
                    } catch (sendEx: Exception) {
                        viewModel.setLocationError("Could not ask for location settings.")
                    }
                } else {
                    viewModel.setLocationError("Location services are disabled on this device.")
                }
            }
        }
    }

    val locationPermissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)

        if (granted) {
            // We have permission! Now let's check if the hardware is actually ON.
            checkDeviceLocationSettings()
        } else {
            viewModel.setLocationError("App permissions are required to verify attendance.")
        }
    }

   val checkPermission = {
       val hasPermission = ContextCompat.checkSelfPermission(
           context, Manifest.permission.ACCESS_FINE_LOCATION
       ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
           context, Manifest.permission.ACCESS_COARSE_LOCATION
       ) == PackageManager.PERMISSION_GRANTED

       if (hasPermission) {
           // Skip asking for permission and go straight to checking hardware
           checkDeviceLocationSettings()
       } else {
           // Ask for permission first
           locationPermissionRequest.launch(
               arrayOf(
                   Manifest.permission.ACCESS_FINE_LOCATION,
                   Manifest.permission.ACCESS_COARSE_LOCATION
               )
           )
       }
   }
    DisposableEffect(Unit) {
        checkPermission()
        onDispose {
            viewModel.stopTracking()
        }
    }


    val SemToSec = mapOf(
        "1st" to listOf("P1", "P2", "C1", "C2"),
        "2nd" to listOf("P1", "P2", "C1", "C2"),
        "3rd" to listOf("A", "B", "C", "D", "E"),
        "4th" to listOf("A", "B", "C", "D", "E"),
        "5th" to listOf("A", "B", "C", "D", "E"),
        "6th" to listOf("A", "B", "C", "D", "E"),
        "7th" to listOf("A", "B", "C", "D", "E"),
        "8th" to listOf("A", "B", "C", "D", "E")
    )
    val selectedSemList = SemToSec[selectedSem] ?: emptyList()

    when(submissionState){
        is ScannerSubmissionState.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Box(
                    contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator()
                }
                Text("Submitting Attendance!")
            }
        }
        else -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                //I don't think so that now I need scannerState, cause I haven't linked it with database
                when (scannerState) {
                    false -> {
                        when(val currentState = currentLocation){
                            is LocationState.IsScucess -> {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .fillMaxSize()
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        CustomDropdown(
                                            label = "Semester",
                                            options = SemToSec.keys.toList(),
                                            selectedValue = selectedSem,
                                            onValueChange = {
                                                selectedSem = it
                                                selectedSec = "" // Resets the branch if they change the course
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        CustomDropdown(
                                            label = "Section",
                                            options = selectedSemList,
                                            selectedValue = selectedSec,
                                            onValueChange = {
                                                selectedSec = it
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Button(
                                            onClick = {
                                                viewModel.chnageScannerState(true)
                                            },
                                            enabled = (selectedSem.isNotEmpty() && selectedSec.isNotEmpty())
                                        ) {
                                            Icon(
                                                Icons.Default.QrCodeScanner,
                                                contentDescription = "Qr Code Scanner Button"
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Scan")
                                        }
                                    }
                                }
                            }
                            is LocationState.Error -> {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ){
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.nolocation),
                                            contentDescription = "Location Not Found Image",
                                            modifier = Modifier
                                                .fillMaxSize(0.6f)
                                                .aspectRatio(1f),
                                            contentScale = ContentScale.Fit
                                        )
                                        Button(
                                            onClick = {
                                                checkPermission()
                                            }
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.RestartAlt,
                                                    contentDescription = "Restart Location Permission"
                                                )
                                                Text("Enable Location")
                                            }
                                        }
                                    }
                                }
                                Toast.makeText(
                                    context,
                                    currentState.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else -> {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator()
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Trying to locate you!")
                                }

                            }
                        }

                    }

                    true -> {
                        RequestCameraPermission {
                            Box(
                                modifier = Modifier
                                    .size(350.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (viewModel.isScanning) {
                                    CameraScanner(viewModel)
                                } else {
                                    Button(
                                        onClick = {
                                            viewModel.restartScanning()
                                        },
                                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                                    ) {
                                        Text("Scan Again")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    }
