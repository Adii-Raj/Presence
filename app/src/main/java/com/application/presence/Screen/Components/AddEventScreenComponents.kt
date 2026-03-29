package com.application.presence.Screen.Components


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.presence.data.model.EventDataClass
import com.application.presence.data.model.OrganizerInput
import com.application.presence.viewmodel.AddEventViewModel
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.modules.OfflineTileProvider
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun OfflineCampusMap(
    zipMapFile: File,
    pinnedLocation: GeoPoint?,
    radius: Float,
    onLocationPinned: (GeoPoint) -> Unit
) {
    val context = LocalContext.current
    var mapController by remember { mutableStateOf<IMapController?>(null) }
    var locationOverlay by remember { mutableStateOf<MyLocationNewOverlay?>(null) }
    var hasLocationPermission by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .aspectRatio(1f)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).apply {
                    setMultiTouchControls(true)
                    setUseDataConnection(false)
                    mapController = this.controller

                    if (zipMapFile.exists() && zipMapFile.length() > 0) {
                        val offlineProvider = OfflineTileProvider(SimpleRegisterReceiver(ctx), arrayOf(zipMapFile))
                        setTileProvider(offlineProvider)
                        setTileSource(XYTileSource("CampusTiles", 15, 19, 256, ".png", arrayOf()))
                    }


                    val mapEventsReceiver = object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                            p?.let { onLocationPinned(it) }
                            return true
                        }
                        override fun longPressHelper(p: GeoPoint?): Boolean = false
                    }
                    overlays.add(MapEventsOverlay(mapEventsReceiver))

                    minZoomLevel = 16.0
                    maxZoomLevel = 19.0
                    setScrollableAreaLimitDouble(BoundingBox(31.357338, 75.466139, 31.348992, 75.449671))

                    controller.setZoom(18.5)
                    controller.setCenter(GeoPoint(31.353362, 75.458683))
                }
            },
            update = { view ->
                view.overlays.removeAll {
                    (it is Marker && it.id == "pinned_marker") ||
                            (it is Polygon && it.id == "radius_circle")
                }

                pinnedLocation?.let { point ->
                    // 3. Draw the Radius Circle FIRST (so it sits underneath the marker pin)
                    val circle = Polygon(view).apply {
                        id = "radius_circle"
                        // osmdroid has a built-in helper to create a circle polygon
                        points = Polygon.pointsAsCircle(point, radius.toDouble())

                        infoWindow = null
                        // Set colors: Semi-transparent fill, solid outline
                        fillPaint.color = android.graphics.Color.argb(
                            60,
                            33,
                            150,
                            243
                        ) // Light Blue, ~25% opacity
                        outlinePaint.color =
                            android.graphics.Color.rgb(33, 150, 243)   // Solid Blue
                        outlinePaint.strokeWidth = 3f
                    }
                    view.overlays.add(circle)

                    // 4. Draw the Pinned Marker
                    val marker = Marker(view).apply {
                        id = "pinned_marker"
                        position = point
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        infoWindow = null
                    }
                    view.overlays.add(marker)
                }

                    // Force the map to redraw with the new overlays
                    view.invalidate()
            }
        )
    }
}


@Composable
fun LocationPickerUi(
    zipMapFile: File,
    tempPinnedLocation: GeoPoint?,
    onLocationTapped: (GeoPoint) -> Unit,
    radius: Float,
    onRadiusChanged: (Float) -> Unit,
    onSaveClicked: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 1. The Map (Takes up the top portion of the screen)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Choose your event hosting Location")
                OfflineCampusMap(
                    zipMapFile = zipMapFile,
                    pinnedLocation = tempPinnedLocation,
                    onLocationPinned = onLocationTapped,
                    radius = radius
                )
            }
        }

        // 2. The Control Panel (Bottom portion)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 16.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Set Attendance Radius: ${radius.toInt()} meters",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // The Radius Slider (Restricted between 10m and 200m)
                Slider(
                    value = radius,
                    onValueChange = onRadiusChanged,
                    valueRange = 10f..200f,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                //This two are just Temporary
                val text by remember { mutableStateOf("Event name with details") }
                Button(
                    onClick = onSaveClicked,
                    enabled = tempPinnedLocation != null,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    if (tempPinnedLocation == null) {
                        Text("Tap the map to drop a pin")
                    } else {
                        Text("Save Location & Create Event")

                    }
                }
            }
        }
    }
}

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
    var attendanceRadius by remember { mutableFloatStateOf(10f) } // Default 50 meters
    val pinnedLocatioNRadius = viewModel.pinnedLocationRadius.collectAsStateWithLifecycle()

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
        radius = pinnedLocatioNRadius.value,
        onRadiusChanged = { newRadius ->
            // Updates the slider state when the user drags it
            viewModel.updateRadius(newRadius)
        },
        onSaveClicked = {
            // 1. Save the final data to the ViewModel
            tempPinnedLocation?.let { viewModel.setPinnedLocation(it) }

            viewModel.updateLocationStatus(!viewModel.isLocationSaved.value)

        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    onSaveClick:(EventDataClass) -> Unit
){
    var eventName by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf("") }
    var eventTime by remember { mutableStateOf("") }
    var eventLocation by remember { mutableStateOf("") }
    var eventDescription by remember { mutableStateOf("") }
    var eventImage by remember { mutableStateOf("") }
    var eventNote by remember { mutableStateOf("") }

    // 2. State for Organizers (Starts with 1 empty organizer)
    var organizers by remember { mutableStateOf(listOf(OrganizerInput("",""))) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()
    val viewmodel : AddEventViewModel = viewModel()
    val pinnedLocation = viewmodel.pinnedLocation.collectAsStateWithLifecycle().value
    val pinnedLocationRadius = viewmodel.pinnedLocationRadius.collectAsStateWithLifecycle().value


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
                Box(
                    modifier = Modifier
                        .weight(1f)
                ){
                    OutlinedTextField(
                        value = eventDate,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Date") },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                            }
                        }
                    )
                    Box(modifier = Modifier
                        .matchParentSize()
                        .clickable(onClick = {showDatePicker = true})
                        .background(color = Color.Transparent)
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                ){
                    OutlinedTextField(
                        value = eventTime,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Time") },
                        trailingIcon = {
                            IconButton(onClick = { showTimePicker = true }) {
                                Icon(Icons.Default.AccessTime, contentDescription = "Select Time")
                            }
                        }
                    )

                    Box(modifier = Modifier
                        .matchParentSize()
                        .clickable(onClick = {showTimePicker = true})
                        .background(color = Color.Transparent)
                    )
                }

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
                onClick = {
                    onSaveClick(
                        EventDataClass(
                            null,
                            eventName,
                            eventDate,
                            eventTime,
                            eventLocation,
                            eventDescription,
                            organizers,
                            eventImage,
                            eventNote,
                            "${pinnedLocation?.latitude}, ${pinnedLocation?.longitude}",
                            pinnedLocationRadius
                        )
                    )
                }, // When you connect the ViewModel, you'll pass the 'organizers' list here!
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