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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.presence.Screen.Components.CameraScanner
import com.application.presence.Screen.Components.CustomDropdown
import com.application.presence.Screen.Components.RequestCameraPermission
import com.application.presence.viewmodel.ScannerViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel
){
    val scannerState by viewModel.scannerState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var selectedSem by remember { mutableStateOf("") }
    var selectedSec by remember { mutableStateOf("") }

    val SemToSec = mapOf(
        "1st" to listOf("P1", "P2", "C1", "C2"),
        "2nd" to listOf("P1", "P2", "C1", "C2"),
        "3rd" to listOf("A", "B", "C", "D", "E"),
        "4th" to listOf("A", "B", "C", "D", "E"),
        "5th" to listOf("A", "B", "C", "D", "E"),
        "6th" to listOf("A", "B", "C", "D", "E"),
        "7th" to listOf("A", "B", "C", "D", "E"),
        "8th" to listOf("A", "B", "C", "D", "E"),
    )
    val selectedSemList = SemToSec[selectedSem] ?: emptyList()


    //Just use to show scanned text value at the bottom of the scanner
    val text = viewModel.scannedText
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        //I don't think so that now I need scannerState
        when(scannerState){
            false -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CustomDropdown(
                            label = "Semester",
                            options = SemToSec.keys.toList(),
                            selectedValue = selectedSem,
                            onValueChange = {
                                selectedSem = it
                                selectedSec = "" // Resets the branch if they change the course
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        CustomDropdown(
                            label = "Section",
                            options = selectedSemList,
                            selectedValue = selectedSec,
                            onValueChange = {
                                selectedSec = it
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                viewModel.chnageScannerState(true)
                            },
                            enabled = (selectedSem.isNotEmpty() && selectedSec.isNotEmpty())
                        ) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = "Qr Code Scanner Button")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Scan")
                        }
                    }
                }
            }
            true -> {
                RequestCameraPermission {
                    Box(
                        modifier = Modifier
                            .size(370.dp)
                            .clip(RoundedCornerShape(10.dp)),
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
            }


        }
    }
}