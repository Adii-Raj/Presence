package com.application.presence.Screen.Components

import android.Manifest
import android.content.pm.PackageManager
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.application.presence.data.local.FastMyLocationProvider
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
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.io.File

@Composable
fun OfflineCampusMap(
    zipMapFile: File,
    pinnedLocation: GeoPoint?,
    onLocationPinned: (GeoPoint) -> Unit,
    onRequestCurrentLocation: (onSuccess: (GeoPoint) -> Unit, onError: (String) -> Unit) -> Unit
) {
    val context = LocalContext.current
    var mapController by remember { mutableStateOf<IMapController?>(null) }
    var locationOverlay by remember { mutableStateOf<MyLocationNewOverlay?>(null) }
    var hasLocationPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions.values.all { it }
        if (!hasLocationPermission) {
            Toast.makeText(context, "Location permission is required", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasFine) {
            hasLocationPermission = true
        } else {
            permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

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

                    val myLocation = MyLocationNewOverlay(FastMyLocationProvider(ctx), this).apply {
                        enableMyLocation()
                    }
                    locationOverlay = myLocation
                    overlays.add(myLocation)

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

                    controller.setZoom(17.5)
                    controller.setCenter(GeoPoint(31.353371, 75.454005))
                }
            },
            update = { view ->
                // Refresh Blue Dot if permission granted
                if (hasLocationPermission && locationOverlay?.isMyLocationEnabled == false) {
                    locationOverlay?.enableMyLocation()
                }

                // Redraw Pinned Marker
                view.overlays.removeAll { it is Marker && it.id == "pinned_marker" }
                pinnedLocation?.let { point ->
                    val marker = Marker(view).apply {
                        id = "pinned_marker"
                        position = point
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Selected Location"
                    }
                    view.overlays.add(marker)
                    view.invalidate()
                }
            }
        )

        FloatingActionButton(
            onClick = {
                // UI simply asks ViewModel for data, handles success/error via callbacks
                onRequestCurrentLocation(
                    /* onSuccess */ { userPoint ->
                        mapController?.animateTo(userPoint)
                        mapController?.setZoom(18.5)
                    },
                    /* onError */ { errorMessage ->
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = "Center on my location")
        }
    }
}


@Composable
fun LocationPickerUi(
    zipMapFile: File,
    tempPinnedLocation: GeoPoint?,
    onLocationTapped: (GeoPoint) -> Unit,
    radius: Float,
    onRadiusChanged: (Float) -> Unit,
    onRequestCurrentLocation: (onSuccess: (GeoPoint) -> Unit, onError: (String) -> Unit) -> Unit,
    onSaveClicked: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        // 1. The Map (Takes up the top portion of the screen)
        Box(modifier = Modifier.weight(1f)) {
            OfflineCampusMap(
                zipMapFile = zipMapFile,
                pinnedLocation = tempPinnedLocation,
                onLocationPinned = onLocationTapped,
                onRequestCurrentLocation = onRequestCurrentLocation
            )
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

                // The Save Button (Disabled until the user actually drops a pin!)
                Button(
                    onClick = onSaveClicked,
                    enabled = tempPinnedLocation != null,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    if (tempPinnedLocation == null) {
                        Text("Tap the map to drop a pin")
                    } else {
                        Text("Save Location & Generate QR")
                    }
                }
            }
        }
    }
}

@Composable
fun QrGeneratorUi(onResetClicked: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // Placeholder for your future QR Code Image
            Surface(
                modifier = Modifier.size(250.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "[ QR Code Generator ]",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Students can now scan this code.",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            // A handy reset button while you are building/testing
            OutlinedButton(onClick = onResetClicked) {
                Text("Pick a new location")
            }
        }
    }
}