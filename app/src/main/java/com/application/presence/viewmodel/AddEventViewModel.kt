package com.application.presence.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.application.presence.data.model.EventDataClass
import com.application.presence.data.state.EventInsertState
import com.application.presence.repository.AddEventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import java.io.File

class AddEventViewModel(
    application: Application): AndroidViewModel(application) {
    
    private val _isLocationSaved = MutableStateFlow(false)
    val isLocationSaved: StateFlow<Boolean> = _isLocationSaved
    private val repository = AddEventRepository(application)
    private val _mapFile = MutableStateFlow<File?>(null)
    val mapFile: StateFlow<File?> = _mapFile.asStateFlow()
    
    private val _pinnedLocation = MutableStateFlow<GeoPoint?>(null)
    val pinnedLocation: StateFlow<GeoPoint?> = _pinnedLocation.asStateFlow()

    private val _pinnedLocationRadius = MutableStateFlow(45.0f)
    val pinnedLocationRadius : StateFlow<Float> = _pinnedLocationRadius

    init {
        loadMapFile("CampusTiles.zip")
    }


    private fun loadMapFile(fileName: String) {
        viewModelScope.launch {
            _mapFile.value = repository.getOfflineMapFile(fileName)
        }
    }

    fun setPinnedLocation(point: GeoPoint) {
        _pinnedLocation.value = point
    }

    fun updateRadius(radius: Float){
        _pinnedLocationRadius.value = radius
    }

    fun updateLocationStatus(result: Boolean){
        _isLocationSaved.value = result
    }

    private val _insertState = MutableStateFlow<EventInsertState>(EventInsertState.Idle)
    val insertState: StateFlow<EventInsertState> = _insertState

    fun insertProfile(eventDataClass: EventDataClass){
        viewModelScope.launch {
            _insertState.value = EventInsertState.IsLoading
            try {
                repository.insertEvent(eventDataClass)
                _insertState.value = EventInsertState.IsSuccess
            }catch (e:Exception){
                _insertState.value = EventInsertState.Error("Error Occurred: ${e.message}")
            }
        }
    }

    fun updateEvent(eventDataClass: EventDataClass) {
        viewModelScope.launch {
            _insertState.value = EventInsertState.IsLoading
            try {
                repository.updateEvent(eventDataClass)
                _insertState.value = EventInsertState.IsSuccess
            } catch (e: Exception) {
                _insertState.value = EventInsertState.Error("Error Updating: ${e.message}")
            }
        }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            _insertState.value = EventInsertState.IsLoading
            try {
                repository.deleteEvent(eventId)
                _insertState.value = EventInsertState.IsSuccess
            } catch (e: Exception) {
                _insertState.value = EventInsertState.Error("Error Deleting: ${e.message}")
            }
        }
    }

    fun resetInsertState() {
        _insertState.value = EventInsertState.Idle
    }
}
