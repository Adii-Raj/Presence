package com.application.presence.Screen


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import com.application.presence.Screen.Components.CenteredBottomNavigation
import com.application.presence.Screen.Components.EventDetailsSheetContent
import com.application.presence.Screen.Components.EventItemCard
import com.application.presence.Screen.Components.ExpandableFab
import com.application.presence.Screen.Components.ReusableDrawer
import com.application.presence.data.model.EventDataClass
import com.application.presence.data.model.Organiser
import com.application.presence.data.state.EventState
import com.application.presence.viewmodel.EventViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewmodel: EventViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showEventDetails by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<EventDataClass?>(null) }
    val userHasSpecialPermission = true


    val eventState = viewmodel.eventState.collectAsState().value

    //Dummy Data
    val dummyEvents = listOf(
        EventDataClass(
            id = "1",
            Event_Name = "Jetpack Compose Workshop",
            Event_Date = "Oct 25, 2026",
            Event_Time = "10:00 AM",
            Event_Location = "Main Auditorium",
            Event_Description = "Learn the fundamentals of building modern native UI with Jetpack Compose.",
            Event_Organiser = listOf(Organiser("Tech Club", "+91 9876543210")),
            Event_Image = "https://scontent.faip1-2.fna.fbcdn.net/v/t39.30808-6/482355322_946025021074050_2897878751535385629_n.jpg?_nc_cat=110&ccb=1-7&_nc_sid=2a1932&_nc_ohc=VX3SLOh9HWgQ7kNvwEQ2E9G&_nc_oc=Adq3tKjtgZmzJBjjhjy9M9U8bbTHi1z03mE512msfOSx4NiNtP8UAS_3SMARRC7u3boSCi7eB871UojyhLjhzVoL&_nc_zt=23&_nc_ht=scontent.faip1-2.fna&_nc_gid=ZMed79p2BVGSq2K3owdbgA&_nc_ss=7a30f&oh=00_AfzZB0GKxJ6CdnoJsarf63OAD-64hwcCHcJVxNF0lml5qA&oe=69C6F2C6",
            Event_Note = "Bring your laptops with Android Studio installed."
        ),
        EventDataClass(
            id = "2",
            Event_Name = "Garud Defenders Meeting",
            Event_Date = "Oct 25, 2026",
            Event_Time = "05:00 PM",
            Event_Location = "AB1",
            Event_Description = "Match Strategy and discussion.",
            Event_Organiser = listOf(Organiser("Sudhakar Kumar", "+91 9876543210")),
            Event_Image = "https://www.collegebatch.com/static/clg-gallery/i-k-gujral-punjab-technical-university-jalandhar-248264.webp",
            Event_Note = "Don't bring your Phones."
        )
    )


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
            when(eventState){
                is EventState.Success -> {
                    val realEvents = eventState.data
                    if(realEvents.isEmpty()){
                        Text("No upcoming events found.")
                    }else{
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                // Add extra bottom padding so the floating nav doesn't cover the last list item
                                .padding(paddingValues)
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item { Spacer(modifier = Modifier.height(8.dp)) } // Top spacing
                            items(realEvents) { eventName ->
                                EventItemCard(
                                    event = eventName,
                                    onKnowMoreClick = {
                                        selectedEvent = eventName
                                        showEventDetails = true
                                    }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(80.dp)) } // Bottom spacing for the floating bar
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