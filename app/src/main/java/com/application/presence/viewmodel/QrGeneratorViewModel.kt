package com.application.presence.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.core.graphics.set
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.application.presence.repository.QrGeneratorRepository
import com.google.android.gms.location.LocationServices
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import java.io.File

class QrGeneratorViewModel(application: Application) : AndroidViewModel(application) {
    //This holds qr Bitmap
    private val _qrBitmap = mutableStateOf<Bitmap?>(null)
    val qrBitmap = _qrBitmap
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


    fun generateQr(text:String){
        if(text.isEmpty()) return
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 600, 600)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)

        for(x in 0 until width){
            for(y in 0 until height){
                bitmap[x, y] = if (bitMatrix[x, y]) android.graphics.Color.BLACK
                else android.graphics.Color.WHITE
            }
        }

        _qrBitmap.value = bitmap
    }

}