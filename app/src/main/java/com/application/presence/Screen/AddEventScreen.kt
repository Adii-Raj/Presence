package com.application.presence.Screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.presence.Screen.Components.DetailScreen
import com.application.presence.Screen.Components.LocationPickerUi
import com.application.presence.Screen.Components.MapPicker
import com.application.presence.Screen.Components.TimePickerDialog
import com.application.presence.data.model.OrganizerInput
import com.application.presence.viewmodel.AddEventViewModel
import com.application.presence.viewmodel.QrGeneratorViewModel
import org.osmdroid.util.GeoPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun AddEventScreenUI(
    onSaveClick: () -> Unit
) {
    val viewModel: AddEventViewModel = viewModel()
    val IsLocationSaved = viewModel.isLocationSaved.collectAsStateWithLifecycle().value
    if(IsLocationSaved){
        DetailScreen { onSaveClick() }
    }
    else{
        MapPicker()
    }
}


@Preview(showBackground = true)
@Composable
fun Showing(){
    AddEventScreenUI({})
}