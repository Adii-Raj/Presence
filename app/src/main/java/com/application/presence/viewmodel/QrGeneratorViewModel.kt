package com.application.presence.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.graphics.set
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.application.presence.data.TotpGenerator
import com.application.presence.data.model.QrUiState
import com.application.presence.repository.AddEventRepository
import com.google.android.gms.location.LocationServices
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import java.io.File

class QrGeneratorViewModel(application: Application) : AndroidViewModel(application) {
    //This holds qr Bitmap
    private val _qrBitmap = mutableStateOf<Bitmap?>(null)
    val qrBitmap = _qrBitmap

    /*
    init {
        generateQr("EventName took from somewhere IDK")
    }
    */

    fun generateQr(text: String) {
        if (text.isEmpty()) return
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 600, 600)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap[x, y] = if (bitMatrix[x, y]) android.graphics.Color.BLACK
                else android.graphics.Color.WHITE
            }
        }

        _qrBitmap.value = bitmap
    }


    // Internal state that can be updated by the ViewModel
    private val _uiState = MutableStateFlow(QrUiState())

    // Public state that the UI can observe (but cannot change directly)
    val uiState: StateFlow<QrUiState> = _uiState.asStateFlow()

    // Keep track of the coroutine job so we can cancel it if the ViewModel is destroyed
    private var timerJob: Job? = null

    // Cache the block number so we only run the heavy math once every 10 seconds
    private var lastCalculatedBlockNumber = -1L
    private var currentTotpCode = ""

    /**
     * Call this from your UI the moment you have fetched the secretKey and uniqueTag from Supabase.
     */
    fun generateQrText(secretKey: String, uniqueTag: String, timeStepSeconds: Long = 10L) {
        // Cancel any existing timer to prevent duplicates if this function is called twice
        timerJob?.cancel()

        // Launch a coroutine tied to the ViewModel's lifecycle
        timerJob = viewModelScope.launch {

            // Loop infinitely as long as the ViewModel is alive
            while (isActive) {
                val currentTimeMillis = System.currentTimeMillis()

                // 1. Calculate how many seconds are left in the current block
                val currentSecondOfBlock = (currentTimeMillis / 1000) % timeStepSeconds
                val secondsRemaining = timeStepSeconds - currentSecondOfBlock

                // 2. Calculate which 10-second block we are currently in
                val currentBlockNumber = currentTimeMillis / (timeStepSeconds * 1000)

                // 3. Optimization: Only run the heavy cryptographic math if the block has changed!
                if (currentBlockNumber != lastCalculatedBlockNumber) {
                    currentTotpCode =
                        TotpGenerator.generateCode(secretKey, currentTimeMillis, timeStepSeconds)
                    lastCalculatedBlockNumber = currentBlockNumber
                }

                // 4. Update the UI state. Compose will automatically detect this and redraw.
                _uiState.value = QrUiState(
                    qrPayload = "$uniqueTag:$currentTotpCode",
                    secondsRemaining = secondsRemaining,
                    isLoading = false
                )

                // 5. Wait exactly 1 second before looping again
                delay(1000L)
            }
        }
    }
    override fun onCleared() {
        super.onCleared()
        // Good practice: ensure the timer stops when the user leaves the screen
        timerJob?.cancel()
    }
}