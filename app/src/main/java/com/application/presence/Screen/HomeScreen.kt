package com.application.presence.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.presence.Screen.Components.CenteredBottomNavigation
import com.application.presence.Screen.Components.EventItemCard
import com.application.presence.Screen.Components.ExpandableFab
import com.application.presence.Screen.Components.ReusableDrawer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showEventDetails by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf("") }
    val userHasSpecialPermission = true

    val upcomingEvents = listOf("Tech Symposium 2026", "Hackathon Alpha", "Alumni Meet")

    ReusableDrawer(drawerState = drawerState) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Hi Aditya!") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Open Menu")
                        }
                    },
                    actions = {
                        // 1. Settings moved to Top Right
                        IconButton(onClick = { /* TODO: Open Settings */ }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                )
            },
            // The custom bottom bar replaces the standard Scaffold bottomBar
            bottomBar = {
                CenteredBottomNavigation()
            },
            floatingActionButton = {

                ExpandableFab(userHasSpecialPermission = userHasSpecialPermission)
            },
            floatingActionButtonPosition = FabPosition.End
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    // Add extra bottom padding so the floating nav doesn't cover the last list item
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) } // Top spacing
                items(upcomingEvents) { eventName ->
                    EventItemCard(
                        eventName = eventName,
                        onLearnMoreClick = {
                            selectedEvent = eventName
                            showEventDetails = true
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) } // Bottom spacing for the floating bar
            }
        }
    }

    // Slide-up Bottom Sheet for Event Details
    if (showEventDetails) {
        ModalBottomSheet(
            onDismissRequest = { showEventDetails = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = selectedEvent, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Here are the detailed descriptions, timings, and venue information for $selectedEvent. You can slide this panel down to dismiss it.")
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { showEventDetails = false }) {
                    Text("Close")
                }
            }
        }
    }
}