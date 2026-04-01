package com.application.presence.viewmodel

import android.app.Application
import android.content.Context
import android.location.Location
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.application.presence.data.FastMyLocationProvider
import com.application.presence.data.model.LocationState
import com.application.presence.data.model.Profile
import com.application.presence.data.state.ScannerSubmissionState
import com.application.presence.repository.ScannerRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider
import kotlinx.coroutines.Job

class ScannerViewModel(
    application: Application,
    private val locationProvider: FastMyLocationProvider,
    onSubmissionSuccess:() -> Unit
): ViewModel() {
    private val _isDeveloperModeEnabled = MutableStateFlow(false)
    val isDeveloperModeEnabled = _isDeveloperModeEnabled

    val repository = ScannerRepository()
    init {
        checkDeveloperMode(application)
        if(!isDeveloperModeEnabled.value){
            Toast.makeText(
                application,
                "Disable Developer Mode!",
                Toast.LENGTH_SHORT
            ).show()
            onSubmissionSuccess()
        }
        fetchProfile()
    }


    var scannedText by mutableStateOf("")
        private set
    var isScanning by mutableStateOf(true)
        private set
    private val _scannerState = MutableStateFlow(false)
    val scannerState: StateFlow<Boolean> = _scannerState
    private  val _submissionState = MutableStateFlow<ScannerSubmissionState>(ScannerSubmissionState.Stopped)
    val submissionState: StateFlow<ScannerSubmissionState> = _submissionState

    private val _locationState = MutableStateFlow<LocationState>(LocationState.IsLoading)
    val locationState: StateFlow<LocationState> = _locationState.asStateFlow()


    private fun checkDeveloperMode(context: Application) {
        val isEnabled = Settings.Global.getInt(
            context.contentResolver,
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
            0
        ) != 0
        _isDeveloperModeEnabled.value = isEnabled
    }
    fun chnageScannerState(value: Boolean){
        _scannerState.value = value
    }
    fun changeSubmissionState(value: ScannerSubmissionState){
        _submissionState.value = value
    }

    fun onQrScanned(result:String, application: Application){
        checkDeveloperMode(application)
        if(!isScanning) return
        scannedText = result
        isScanning = false
        _scannerState.value = false
        _submissionState.value = ScannerSubmissionState.Loading

        val currentLocation = _locationState.value
        val currentProfile = _profileState.value
        if(!isDeveloperModeEnabled.value){
            Toast.makeText(
                application,
                "Disable Developer Mode!",
                Toast.LENGTH_SHORT
            ).show()
        }
        else{
            if (currentLocation is LocationState.IsScucess && currentProfile != null) {

                val decodedValues = decodeScannedText()

                if (decodedValues != null) {
                    verifyScannedQr(
                        uniqueTag = decodedValues.first,
                        scannedCode = decodedValues.second,
                        studentRoll = currentProfile.roll,
                        userLatitude = currentLocation.latitude.toDoubleOrNull() ?: 0.0,
                        userLongitude = currentLocation.longitude.toDoubleOrNull() ?: 0.0
                    )
                } else {
                    _submissionState.value = ScannerSubmissionState.Error("Invalid QR Code Format")
                }
            } else {
                _submissionState.value = ScannerSubmissionState.Error("Missing GPS Location or Profile Data")
            }
        }
    }
    fun restartScanning(){
        isScanning=true
        scannedText = ""
        _submissionState.value = ScannerSubmissionState.Stopped
    }
    fun decodeScannedText(): Pair<String, String>?{
        val splitText = scannedText.split(":")
        if(splitText.size==2){
            val uniqueTag = splitText[0].trim()
            val secretKey = splitText[1].trim()
            return Pair(uniqueTag, secretKey)
        }
        return null
    }

    fun verifyScannedQr(
        uniqueTag: String,
        scannedCode: String,
        studentRoll: String?,
        userLatitude: Double,
        userLongitude: Double
        ){
        viewModelScope.launch {
            _submissionState.value = repository.verifyScannedQr(
                uniqueTag,
                scannedCode,
                studentRoll!!,
                userLatitude,
                userLongitude
                )
        }
    }

    private val _profileState = MutableStateFlow<Profile?>(null)
    val profileState: StateFlow<Profile?> = _profileState.asStateFlow()

    fun fetchProfile() {
        viewModelScope.launch {
            val fetchedProfile = repository.getProfile()
            _profileState.value = fetchedProfile
        }
    }

    // We will keep track of our timeout job so we can cancel it if we succeed early
    private var timeoutJob: Job? = null

    fun changeLocationState(locationState: LocationState){
        _locationState.value = locationState
    }
    private val locationConsumer = object : IMyLocationConsumer {
        override fun onLocationChanged(location: Location?, source: IMyLocationProvider?) {
            if (location != null) {
                // 1. We got a valid location! Cancel the error timeout.
                timeoutJob?.cancel()

                // 2. Push the success state to the UI
                _locationState.value = LocationState.IsScucess(
                    latitude = location.latitude.toString(),
                    longitude = location.longitude.toString()
                )
            }
            // 3. NOTICE: There is no 'else' block here anymore!
            // If location is null, we do absolutely nothing. We just let it
            // stay in 'IsLoading' and wait for the next ping.
        }
    }

    fun startTracking() {
        // Reset state to loading
        _locationState.value = LocationState.IsLoading

        // Start the hardware provider
        val success = locationProvider.startLocationProvider(locationConsumer)

        if (!success) {
            _locationState.value = LocationState.Error("Failed to initialize GPS hardware.")
            return
        }

        // 4. Start the Timeout Countdown
        timeoutJob = viewModelScope.launch {
            delay(20000) // Give it 20 seconds to find a satellite

            // If 20 seconds pass and we are STILL stuck in the Loading state...
            if (_locationState.value is LocationState.IsLoading) {
                // Force the error state
                _locationState.value = LocationState.Error("GPS signal is too weak. Contact coordinator for attendance.")
                // Stop the hardware to save battery
                stopTracking()
            }
        }
    }
    fun stopTracking() {
        timeoutJob?.cancel() // Stop the timer if it's running
        locationProvider.stopLocationProvider()
    }

    // Failsafe: Ensure tracking stops if the ViewModel is destroyed
    override fun onCleared() {
        super.onCleared()
        stopTracking()
    }

    fun setLocationError(message: String) {
        _locationState.value = LocationState.Error(message)
        stopTracking()
    }


}