package com.application.presence.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.application.presence.repository.QrGeneratorRepository
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import java.io.File

class QrGeneratorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = QrGeneratorRepository(application)

    private val _mapFile = MutableStateFlow<File?>(null)
    val mapFile: StateFlow<File?> = _mapFile.asStateFlow()

    // MVVM: ViewModel holds the state of the pinned location
    private val _pinnedLocation = MutableStateFlow<GeoPoint?>(null)
    val pinnedLocation: StateFlow<GeoPoint?> = _pinnedLocation.asStateFlow()

    init {
        loadMapFile("CampusTiles.zip")
    }

    private fun loadMapFile(fileName: String) {
        viewModelScope.launch {
            _mapFile.value = repository.getOfflineMapFile(fileName)
        }
    }

    // MVVM: UI sends the click event here
    fun setPinnedLocation(point: GeoPoint) {
        _pinnedLocation.value = point
    }

    // MVVM: ViewModel handles the hardware location request, passing success/error back to UI
    @SuppressLint("MissingPermission")
    fun fetchDeviceLocation(onSuccess: (GeoPoint) -> Unit, onError: (String) -> Unit) {
        val context = getApplication<Application>()
        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            onError("Location permission not granted yet")
            return
        }

        val client = LocationServices.getFusedLocationProviderClient(context)
        client.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onSuccess(GeoPoint(location.latitude, location.longitude))
            } else {
                onError("Please turn on your phone's GPS")
            }
        }.addOnFailureListener {
            onError("Failed to get location")
        }
    }
}