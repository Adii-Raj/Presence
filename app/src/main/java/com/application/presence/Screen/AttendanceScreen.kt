package com.application.presence.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.application.presence.data.model.StudentAttendance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    // Data
    eventsList: List<String>, // List of event names for the dropdown
    selectedEvent: String,
    studentList: List<StudentAttendance>,
    selectedStudentIds: Set<String>, // Tracks which checkboxes are ticked

    // Actions
    onEventSelected: (String) -> Unit,
    onStudentSelectionChange: (String, Boolean) -> Unit,
    onClearSelection: () -> Unit,
    onDeleteSelectedClick: () -> Unit,
    onDownloadExcelClick: () -> Unit
) {

    // For horizontal scrolling of the table
    val horizontalScrollState = rememberScrollState()

    Scaffold(
        topBar = {
            // Contextual Top App Bar: Changes if items are selected!
            if (selectedStudentIds.isNotEmpty()) {
                TopAppBar(
                    title = { Text("${selectedStudentIds.size} Selected") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    navigationIcon = {
                        IconButton(onClick = onClearSelection) {
                            Icon(Icons.Default.Close, contentDescription = "Clear Selection")
                        }
                    },
                    actions = {
                        IconButton(onClick = onDeleteSelectedClick) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Selected", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = { Text("Manage Attendance", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(
                            onClick = onDownloadExcelClick,
                            // Only allow download if an event is selected and there are students
                            enabled = selectedEvent.isNotEmpty() && studentList.isNotEmpty()
                        ) {
                            Icon(Icons.Default.Download, contentDescription = "Download Excel")
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- DROPDOWN FOR EVENT SELECTION ---
            var expanded by remember { mutableStateOf(false) }

            Box(modifier = Modifier.padding(16.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedEvent.ifEmpty { "Select an Event" },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Event") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        eventsList.forEach { eventName ->
                            DropdownMenuItem(
                                text = { Text(eventName) },
                                onClick = {
                                    onEventSelected(eventName)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- THE ATTENDANCE TABLE ---
            if (selectedEvent.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Please select an event to view attendance.", color = Color.Gray)
                }
            } else if (studentList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No attendance recorded for this event yet.", color = Color.Gray)
                }
            } else {
                // We apply horizontalScroll to the LazyColumn so the columns don't get squished
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .horizontalScroll(horizontalScrollState)
                ) {
                    // 1. Table Header
                    item {
                        Row(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Select", fontWeight = FontWeight.Bold, modifier = Modifier.width(60.dp))
                            Text("Name", fontWeight = FontWeight.Bold, modifier = Modifier.width(140.dp))
                            Text("Roll No", fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp))
                            Text("Phone", fontWeight = FontWeight.Bold, modifier = Modifier.width(120.dp))
                            Text("Sec", fontWeight = FontWeight.Bold, modifier = Modifier.width(60.dp))
                            Text("Sem", fontWeight = FontWeight.Bold, modifier = Modifier.width(60.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                        HorizontalDivider()
                    }

                    // 2. Table Rows (Students)
                    items(studentList) { student ->
                        val isSelected = selectedStudentIds.contains(student.id)

                        Row(
                            modifier = Modifier
                                // Highlight the row if it's selected
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else Color.Transparent)
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.width(16.dp))

                            // Checkbox
                            Box(modifier = Modifier.width(60.dp), contentAlignment = Alignment.CenterStart) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { isChecked ->
                                        onStudentSelectionChange(student.id, isChecked)
                                    }
                                )
                            }

                            // Data Columns
                            Text(student.name, modifier = Modifier.width(140.dp), maxLines = 1)
                            Text(student.rollNumber, modifier = Modifier.width(100.dp), maxLines = 1)
                            Text(student.phone, modifier = Modifier.width(120.dp), maxLines = 1)
                            Text(student.section, modifier = Modifier.width(60.dp), maxLines = 1)
                            Text(student.semester, modifier = Modifier.width(60.dp), maxLines = 1)

                            Spacer(modifier = Modifier.width(16.dp))
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}