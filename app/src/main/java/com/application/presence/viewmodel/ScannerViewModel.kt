package com.application.presence.viewmodel

import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.application.presence.data.FastMyLocationProvider
import com.application.presence.data.state.ScannerSubmissionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider

class ScannerViewModel(
    private val locationProvider: FastMyLocationProvider,
    onSubmissionSuccess:() -> Unit
): ViewModel() {
    var scannedText by mutableStateOf("")
        private set
    var isScanning by mutableStateOf(true)
        private set
    private val _scannerState = MutableStateFlow(false)
    val scannerState: StateFlow<Boolean> = _scannerState
    private  val _submissionState = MutableStateFlow<ScannerSubmissionState>(ScannerSubmissionState.Stopped)
    val submissionState: StateFlow<ScannerSubmissionState> = _submissionState



    fun chnageScannerState(value: Boolean){
        _scannerState.value = value
    }

    fun onQrScanned(result:String){
        if(!isScanning) return
        scannedText = result
        isScanning = false
        _scannerState.value = false
        _submissionState.value = ScannerSubmissionState.Loading
    }
    fun restartScanning(){
        isScanning=true
        scannedText = ""
        _submissionState.value = ScannerSubmissionState.Stopped
    }


    fun isQrValid(verificationText:String):String{
        return "EventId"
    }

    fun addAttendance(
        eventName:String,
        userName:String,
        roll: String,
        semester:String,
        phone: String,
        section:String,
        submissionDate:String,
        submissionTime:String
    ){
    //I think I need to change, function of isQrValid and addAttendance
    }


    // 1. Private mutable state - only the ViewModel can change this
    private val _locationState = MutableStateFlow<Location?>(null)

    // 2. Public immutable state - the UI observes this
    val locationState: StateFlow<Location?> = _locationState.asStateFlow()

    // 3. The consumer that listens to your FastMyLocationProvider
    private val locationConsumer = object : IMyLocationConsumer {
        override fun onLocationChanged(location: Location?, source: IMyLocationProvider?) {
            // Update the StateFlow every time a new location arrives
            _locationState.value = location
        }
    }

    fun startTracking() {
        // Safe to call multiple times, provider handles its own state
        locationProvider.startLocationProvider(locationConsumer)
    }

    fun stopTracking() {
        locationProvider.stopLocationProvider()
    }

    // Failsafe: Ensure tracking stops if the ViewModel is destroyed
    override fun onCleared() {
        super.onCleared()
        stopTracking()
    }
}