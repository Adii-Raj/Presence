package com.application.presence.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.presence.viewmodel.QrGeneratorViewModel
import org.osmdroid.util.GeoPoint
import androidx.compose.material3.*
import com.application.presence.Screen.Components.LocationPickerUi
import com.application.presence.Screen.Components.QrGeneratorUi
import java.io.File

@Composable
fun QrGeneratorScreen(viewModel: QrGeneratorViewModel = viewModel()) {
    val mapFile by viewModel.mapFile.collectAsState()

    // We use a simple boolean to toggle the UI for now.
    // Later, you can bind this to your ViewModel state!
    var isLocationSaved by remember { mutableStateOf(false) }

    // Temporary states to hold the user's choices BEFORE they click save
    var tempPinnedLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var attendanceRadius by remember { mutableFloatStateOf(50f) } // Default 50 meters

    if (mapFile == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        if (!isLocationSaved) {
            // --- SCREEN 1: Map Picker & Controls ---
            LocationPickerUi(
                zipMapFile = mapFile!!,
                tempPinnedLocation = tempPinnedLocation,
                onLocationTapped = { point ->
                    // Updates the temporary state when the user taps the map
                    tempPinnedLocation = point
                },
                radius = attendanceRadius,
                onRadiusChanged = { newRadius ->
                    // Updates the slider state when the user drags it
                    attendanceRadius = newRadius
                },
                onRequestCurrentLocation = { onSuccess, onError ->
                    // Calls the ViewModel's GPS function when the floating button is clicked
                    viewModel.fetchDeviceLocation(onSuccess, onError)
                },
                onSaveClicked = {
                    // 1. Save the final data to the ViewModel
                    if (tempPinnedLocation != null) {
                        viewModel.setPinnedLocation(tempPinnedLocation!!)
                        // TODO: If you create a setRadius() in your ViewModel, call it here too!
                    }

                    // 2. Flip the switch to hide the map and show the QR screen
                    isLocationSaved = true
                }
            )
        } else {
            // --- SCREEN 2: QR Generator ---
            QrGeneratorUi(
                onResetClicked = {
                    // Helpful for testing: lets you go back to the map
                    isLocationSaved = false
                    tempPinnedLocation = null
                }
            )
        }
    }
}
