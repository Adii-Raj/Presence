package com.application.presence.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.presence.viewmodel.QrGeneratorViewModel
import org.osmdroid.util.GeoPoint
import androidx.compose.material3.*
import com.application.presence.Screen.Components.LocationPickerUi
import com.application.presence.Screen.Components.QrGeneratorUi
import com.application.presence.Screen.Components.QrScreen
import java.io.File

@Composable
fun QrGeneratorScreen() {
    QrGeneratorUi()
}
