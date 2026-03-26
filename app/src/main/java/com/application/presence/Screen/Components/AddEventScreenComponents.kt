package com.application.presence.Screen.Components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.presence.data.model.OrganizerInput
import com.application.presence.viewmodel.AddEventViewModel
import com.application.presence.viewmodel.QrGeneratorViewModel
import org.osmdroid.util.GeoPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// Don't forget this wrapper for the TimePicker if you haven't added it yet!
@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(title) },
        text = { content() },
        dismissButton = {
            TextButton(onClick = onCancel) { Text("Cancel") }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("OK") }
        }
    )
}

@Composable
fun MapPicker(viewModel: AddEventViewModel = viewModel()){
    val mapFile by viewModel.mapFile.collectAsState()
    // Temporary states to hold the user's choices BEFORE they click save
    var tempPinnedLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var attendanceRadius by remember { mutableFloatStateOf(50f) } // Default 50 meters

    if (mapFile == null) {
        // Show a loading spinner centered on the screen while the file is prepared
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return // Stop executing the rest of the composable until mapFile is ready
    }

    LocationPickerUi(
        zipMapFile = mapFile!!,
        tempPinnedLocation = tempPinnedLocation,
        onLocationTapped = { point ->
            // Updates the temporary state when the user taps the map
            tempPinnedLocation = point
        },
        radius = attendanceRadius,
        onRadiusChanged = { newRadius ->
            // Updates the slider state when the user drags it
            attendanceRadius = newRadius
        },
        onSaveClicked = {
            // 1. Save the final data to the ViewModel
            if (tempPinnedLocation != null) {
                viewModel.setPinnedLocation(tempPinnedLocation!!)
                // TODO: If you create a setRadius() in your ViewModel, call it here too!
            }

            viewModel.updateLocationStatus(!viewModel.isLocationSaved.value)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    onSaveClick:() -> Unit
){
    var eventName by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf("") }
    var eventTime by remember { mutableStateOf("") }
    var eventLocation by remember { mutableStateOf("") }
    var eventDescription by remember { mutableStateOf("") }
    var eventImage by remember { mutableStateOf("") }
    var eventNote by remember { mutableStateOf("") }

    // 2. State for Organizers (Starts with 1 empty organizer)
    var organizers by remember { mutableStateOf(listOf(OrganizerInput())) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Event", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = eventName,
                onValueChange = { eventName = it },
                label = { Text("Event Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = eventDate,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Date") },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                        }
                    }
                )

                OutlinedTextField(
                    value = eventTime,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Time") },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        IconButton(onClick = { showTimePicker = true }) {
                            Icon(Icons.Default.AccessTime, contentDescription = "Select Time")
                        }
                    }
                )
            }

            OutlinedTextField(
                value = eventLocation,
                onValueChange = { eventLocation = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = eventDescription,
                onValueChange = { eventDescription = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )

            // --- DYNAMIC ORGANIZERS CARD ---
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Organizer Details", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

                    // 3. Loop through the organizers list
                    organizers.forEachIndexed { index, organizer ->

                        // Add a visual divider between multiple organizers
                        if (index > 0) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Organizer ${index + 1}", style = MaterialTheme.typography.labelMedium)

                                // Show delete button only if there is more than 1 organizer
                                if (organizers.size > 1) {
                                    IconButton(
                                        onClick = {
                                            // Remove this specific organizer from the list
                                            organizers = organizers.toMutableList().apply { removeAt(index) }
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove Organizer", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = organizer.name,
                                onValueChange = { newName ->
                                    // Update the specific organizer's name in the list
                                    val newList = organizers.toMutableList()
                                    newList[index] = organizer.copy(name = newName)
                                    organizers = newList
                                },
                                label = { Text("Name") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next)
                            )
                            OutlinedTextField(
                                value = organizer.phone,
                                onValueChange = { newPhone ->
                                    // Update the specific organizer's phone in the list
                                    val newList = organizers.toMutableList()
                                    newList[index] = organizer.copy(phone = newPhone)
                                    organizers = newList
                                },
                                label = { Text("Phone Number") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
                            )
                        }
                    }

                    // 4. The "Add More" Button
                    TextButton(
                        onClick = {
                            // Append a new, blank organizer to the list
                            organizers = organizers + OrganizerInput("", "")
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Add Another Organizer")
                    }
                }
            }
            // --- END DYNAMIC ORGANIZERS CARD ---

            OutlinedTextField(
                value = eventImage,
                onValueChange = { eventImage = it },
                label = { Text("Image URL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = eventNote,
                onValueChange = { eventNote = it },
                label = { Text("Internal Notes (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Done)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onSaveClick, // When you connect the ViewModel, you'll pass the 'organizers' list here!
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Event, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Save Event", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            eventDate = formatter.format(Date(millis))
                        }
                        showDatePicker = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (showTimePicker) {
            TimePickerDialog(
                onCancel = { showTimePicker = false },
                onConfirm = {
                    eventTime = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }
            ) {
                TimePicker(state = timePickerState)
            }
        }
    }
}