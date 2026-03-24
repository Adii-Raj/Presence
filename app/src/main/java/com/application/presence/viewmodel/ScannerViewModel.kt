package com.application.presence.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.application.presence.data.state.ScannerSubmissionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ScannerViewModel(
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
}