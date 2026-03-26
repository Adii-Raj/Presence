package com.application.presence.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.presence.repository.AddEventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import java.io.File

class AddEventViewModel(application: Application): AndroidViewModel(application) {
    // We use a simple boolean to toggle the UI for now.
    // Later, you can bind this to your ViewModel state!
    private val _isLocationSaved = MutableStateFlow(false)
    val isLocationSaved: StateFlow<Boolean> = _isLocationSaved
    private val repository = AddEventRepository(application)
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

    fun updateLocationStatus(result: Boolean){
        _isLocationSaved.value = result
    }
}