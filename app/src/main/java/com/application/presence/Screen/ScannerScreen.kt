package com.application.presence.Screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.presence.Screen.Components.CameraScanner
import com.application.presence.Screen.Components.RequestCameraPermission
import com.application.presence.viewmodel.ScannerViewModel

@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel = viewModel()
){
    val context = LocalContext.current
    val scannedText = viewModel.scannedText
    //This is used to run website if URL in the QR
    LaunchedEffect(scannedText) {
        if (
            !scannedText.isEmpty() &&
            android.util.Patterns.WEB_URL.matcher(scannedText).matches()
        ){
            val intent = Intent(
                Intent.ACTION_VIEW,
                scannedText.toUri()
            )
            context.startActivity(intent)
            viewModel.restartScanning()
        }
    }


    val text = viewModel.scannedText
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        RequestCameraPermission {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(width = 4.dp, color = Color.Blue, shape = RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                if(viewModel.isScanning){
                    CameraScanner(viewModel)
                }else{
                    Button(
                        onClick = {
                            viewModel.restartScanning()
                        },
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    ) {
                        Text("Scan Again")
                    }
                }
            }
        }
        Text(
            text = "Scanned Result: ",
            fontWeight = FontWeight.Bold
        )
        Text(text = text)

        //Copy to clipboard button
        if(scannedText.isNotBlank()){
            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                val clipboard = context.getSystemService(
                    Context.CLIPBOARD_SERVICE
                )as ClipboardManager

                val clip = ClipData.newPlainText(
                    "Qr Code: ",
                    scannedText
                )
                clipboard.setPrimaryClip(clip)

                Toast.makeText(
                    context,
                    "Copied to clipboard",
                    Toast.LENGTH_SHORT
                ).show()
            }) {
                Text("Copy to Clipboardz")
            }
        }
    }
}