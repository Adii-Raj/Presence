package com.application.presence.Screen.Components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.application.presence.data.model.EventDataClass

@Composable
fun ReusableDrawer(
    drawerState: DrawerState,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Menu",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Home") },
                    selected = true,
                    onClick = { /* Handle Click */ },
                    icon = { Icon(Icons.Default.Home, null) }
                )
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = { /* Handle Click */ },
                    icon = { Icon(Icons.Default.Settings, null) }
                )
                NavigationDrawerItem(
                    label = { Text("Contact") },
                    selected = false,
                    onClick = { /* Handle Click */ },
                    icon = { Icon(Icons.Default.Phone, null) }
                )
                NavigationDrawerItem(
                    label = { Text("About") },
                    selected = false,
                    onClick = { /* Handle Click */ },
                    icon = { Icon(Icons.Default.Info, null) }
                )
            }
        },
        content = content
    )
}


@Composable
fun EventItemCard(event: EventDataClass, onKnowMoreClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = event.Event_Image,
                contentDescription = "Image for ${event.Event_Name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.Event_Name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = onKnowMoreClick) {
                    Text("Know More")
                }
            }
        }
    }
}

@Composable
fun ExpandableFab(userHasSpecialPermission: Boolean) {
    var expanded by remember { mutableStateOf(false) }
    // Animates the + icon turning into an x
    val rotation by animateFloatAsState(targetValue = if (expanded) 45f else 0f, label = "fab_rotation")

    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier.padding(bottom = 16.dp, end = 16.dp)
    ) {
        // The popup options
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                if (userHasSpecialPermission) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Text(
                                text = "Manage Events",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        SmallFloatingActionButton(
                            onClick = { /* TODO: Navigate to Manage */ expanded = false },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Manage")
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = "Add Event",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    SmallFloatingActionButton(
                        onClick = { /* TODO: Navigate to Add */ expanded = false },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            }
        }

        // The Main Trigger Button
        FloatingActionButton(
            onClick = { expanded = !expanded },
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Expand menu",
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}


@Composable
fun CenteredBottomNavigation() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // The Floating Pill Background (Slightly larger now)
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            shadowElevation = 8.dp,
            modifier = Modifier.wrapContentWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                // Increased horizontal and vertical padding to make the bar bigger
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                // 1. Home Button
                IconButton(onClick = { /* TODO */ }) {
                    // Increased icon size to 32.dp
                    Icon(Icons.Default.Home, contentDescription = "Home", modifier = Modifier.size(32.dp))
                }

                // --- CENTER GAP ---
                // A wide, fixed gap to perfectly cradle the scanner button
                Spacer(modifier = Modifier.width(88.dp))

                // 2. Past Events Button
                IconButton(onClick = { /* TODO */ }) {
                    // Increased icon size to 32.dp
                    Icon(Icons.Default.DateRange, contentDescription = "Past Events", modifier = Modifier.size(32.dp))
                }
            }
        }

        // The Lifted Scan QR Button (Bigger and perfectly centered)
        FloatingActionButton(
            onClick = { /* TODO: Scan QR Logic */ },
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-24).dp) // Lifts it up more to match the bigger bar
                .size(72.dp), // Increased overall button size
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan QR", modifier = Modifier.size(36.dp))
        }
    }
}

@Composable
fun EventDetailsSheetContent(event: EventDataClass) {
    // Using a LazyColumn inside the sheet allows scrolling if the description is very long
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp) // Padding for navigation bar clearance
    ) {
        // 1. Top Image (Full Width)
        item {
            AsyncImage(
                model = event.Event_Image,
                contentDescription = "Full Image for ${event.Event_Name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color.DarkGray)
            )
        }

        // 2. Event Details Section
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = event.Event_Name, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                // Date & Time Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = "Date & Time", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    val dateStr = event.Event_Date ?: "TBA"
                    val timeStr = event.Event_Time ?: "TBA"
                    Text(text = "$dateStr | $timeStr", style = MaterialTheme.typography.bodyLarge)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Location Row
                if (event.Event_Location != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = event.Event_Location, style = MaterialTheme.typography.bodyLarge)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Description
                Text(text = "About this event", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = event.Event_Description ?: "No description available.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Organisers
                if (!event.Event_Organiser.isNullOrEmpty()) {
                    Text(text = "Organisers", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    event.Event_Organiser.forEach { organiser ->
                        val name = organiser.name ?: "Unknown"
                        val phone = organiser.phone ?: ""
                        Text(text = "• $name $phone", style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Important Note
                if (event.Event_Note != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Note: ${event.Event_Note}",
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }
        }
    }
}