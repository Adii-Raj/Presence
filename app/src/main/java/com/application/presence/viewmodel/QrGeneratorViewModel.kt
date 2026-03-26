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
import com.application.presence.repository.AddEventRepository
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

    init {
        generateQr("EventName took from somewhere IDK")
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