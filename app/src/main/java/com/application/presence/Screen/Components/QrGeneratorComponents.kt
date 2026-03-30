package com.application.presence.Screen.Components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.presence.data.model.QrUiState
import com.application.presence.viewmodel.QrGeneratorViewModel


@Composable
fun QrGeneratorUi(uiState: QrUiState, viewModel: QrGeneratorViewModel) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
                return@Column
            }

            viewModel.generateQr(uiState.qrPayload)
            // Placeholder for your future QR Code Image
            Surface(
                modifier = Modifier.size(250.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Box(contentAlignment = Alignment.Center) {
                    QrScreen()
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { uiState.secondsRemaining.toFloat() / 10f }, // Assuming 10 sec step
                modifier = Modifier.fillMaxWidth(0.6f),
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Scan QR to mark Attendance.",
                style = MaterialTheme.typography.bodyLarge
            )

        }
    }
}


@Composable
fun QrScreen(viewModel: QrGeneratorViewModel = viewModel()){
    var qrBitmap = viewModel.qrBitmap.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        qrBitmap?.let{bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "QR Code",
                modifier = Modifier.size(250.dp)
            )
        }
    }
}