package com.application.presence.Screen


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.application.presence.Screen.Components.CenteredBottomNavigation
import com.application.presence.Screen.Components.EventDetailsSheetContent
import com.application.presence.Screen.Components.EventItemCard
import com.application.presence.Screen.Components.ExpandableFab
import com.application.presence.Screen.Components.ReusableDrawer
import com.application.presence.data.model.EventDataClass
import com.application.presence.data.state.EventState
import com.application.presence.viewmodel.AuthViewModel
import com.application.presence.viewmodel.EventViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    eventViewmodel: EventViewModel,
    onScannerClick:()-> Unit,
    onAddClick:() -> Unit,
    onManageClick:() -> Unit,
    onQrButtonClicked:() -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showEventDetails by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<EventDataClass?>(null) }
    val userHasSpecialPermission = true


    val eventState = eventViewmodel.eventState.collectAsStateWithLifecycle().value
    val isRefreshing by eventViewmodel.isRefreshing.collectAsState()

    val profile = authViewModel.profile.collectAsStateWithLifecycle()
    val firstName = profile.value?.name?.split(" ")?.getOrNull(0)

    ReusableDrawer(drawerState = drawerState) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Hi ${firstName}! \uD83D\uDC4B") },
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
            bottomBar = {
                CenteredBottomNavigation(onScannerClick)
            },
            floatingActionButton = {
                ExpandableFab(userHasSpecialPermission = userHasSpecialPermission, onAddClick = onAddClick, onManageClick = onManageClick)
            },
            floatingActionButtonPosition = FabPosition.End
        ) { paddingValues ->
            when(eventState){
                is EventState.Success -> {
                    val realEvents = eventState.data

                    if (realEvents.isEmpty()) {
                        Text("No upcoming events found.")
                    } else {
                        PullToRefreshBox(
                            isRefreshing = isRefreshing,
                            onRefresh = {
                                eventViewmodel.getEvent(isRefresh = true)
                            },
                            // modifier = Modifier.padding(paddingValues)
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentPadding = PaddingValues(
                                    top = paddingValues.calculateTopPadding() + 8.dp, // Clears the TopAppBar
                                    bottom = paddingValues.calculateBottomPadding() + 24.dp, // Pushes the last item up past your floating bar
                                    start = 16.dp,
                                    end = 16.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                item { Spacer(modifier = Modifier.height(8.dp)) }

                                items(realEvents) { event ->
                                    EventItemCard(
                                        event = event,
                                        userHasSpecialPermission = userHasSpecialPermission, // Pass it down!
                                        onKnowMoreClick = {
                                            selectedEvent = event
                                            showEventDetails = true
                                        },
                                        onEditClick = {
                                            // TODO: Navigate to Edit Screen for this specific event
                                            // e.g., navController.navigate("edit_event/${event.id}")
                                        },
                                        onQrClick = {
                                            onQrButtonClicked()
                                        }
                                    )
                                }

                                item { Spacer(modifier = Modifier.height(80.dp)) }
                            }
                        }
                    }
                }
                is EventState.Error -> {
                    Text("Error loading events.")
                }
                else -> {CircularProgressIndicator()}
            }
        }
    }

    // Slide-up Bottom Sheet for Event Details
    if (showEventDetails) {
        ModalBottomSheet(
            onDismissRequest = { showEventDetails = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
        ) {
            // Pass the selected event safely to the new component
            selectedEvent?.let { event ->
                EventDetailsSheetContent(event = event)
            }
        }
    }
}