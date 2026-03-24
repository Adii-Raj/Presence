package com.application.presence.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ScannerViewModel: ViewModel() {
    var scannedText by mutableStateOf("")
        private set
    var isScanning by mutableStateOf(true)
        private set


    fun onQrScanned(result:String){
        if(!isScanning) return
        scannedText = result
        isScanning = false
    }
    fun restartScanning(){
        isScanning=true
        scannedText = ""
    }
}