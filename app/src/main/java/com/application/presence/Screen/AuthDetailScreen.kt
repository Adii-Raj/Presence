package com.application.presence.Screen

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.presence.Screen.Components.CustomDropdown
import com.application.presence.data.SupabaseClientProvider
import com.application.presence.data.model.LocalProfile
import com.application.presence.data.model.Profile
import com.application.presence.repository.AuthRepository
import com.application.presence.repository.LocalUserRepository
import com.application.presence.viewmodel.AuthViewModel
import io.github.jan.supabase.auth.auth

@Composable
fun AuthDetailScreen(
    viewModel: AuthViewModel,
    context: Context,
    repo: AuthRepository,
    onSaveClick:()->Unit
){
    val profile by viewModel.profile.collectAsState()
    val supabase = SupabaseClientProvider.client
    val currentUser = supabase.auth.currentUserOrNull()

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(currentUser?.email) }
    var roll by remember { mutableStateOf("") }
    var branch by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var permission by remember { mutableStateOf(profile?.permission) }
    val AndroidId by remember { mutableStateOf(viewModel.getDeviceId()) }



    val CourseToBranch = mapOf(
        "B.Tech" to listOf("CSE", "Civil", "ME", "Electrical"),
        "BBA" to listOf("HR, Finance"),
        "M.Tech" to listOf("CSE", "Civil"),
        "MBA" to listOf("Brach 1", "Branch 2")
    )
    val selectedCourseList = CourseToBranch[course] ?: emptyList()
    val scrollState = rememberScrollState()

    Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp), // Added padding so it doesn't hug the very bottom edge
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Made by Aditya",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        ) { paddingValues ->
            // CHANGED from Box to Column
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(scrollState) // Makes the form scrollable
                    .padding(horizontal = 24.dp, vertical = 16.dp), // Breathing room on the sides
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp) // Automatically adds space between each item!
            ) {

                // Added a title to make it look like a complete screen
                Text(
                    text = "Complete Profile",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth() // Changed to fillMaxWidth for standard mobile design
                )

                OutlinedTextField(
                    value = roll,
                    onValueChange = { roll = it },
                    label = { Text("Roll Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") }, // Fixed copy-paste typo (said "Name")
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone // Changed to Phone for better formatting
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                CustomDropdown(
                    label = "Course",
                    options = CourseToBranch.keys.toList(),
                    selectedValue = course,
                    onValueChange = {
                        course = it
                        branch = "" // Resets the branch if they change the course
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                CustomDropdown(
                    label = "Branch",
                    options = selectedCourseList,
                    selectedValue = branch,
                    onValueChange = { branch = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp)) // Extra space before the button

                Button(
                    onClick = {
                        // PERFORMANCE FIX: Moved profile creation inside the onClick block
                        // Otherwise, Compose rebuilds this object every single time a user types a letter
                        val newSupabaseProfile = Profile(
                            id = currentUser?.id,
                            name = name,
                            gmail_id = email?:"",
                            phone = phone,
                            roll = roll,
                            course = course,
                            branch = branch,
                            device_fingerprint = AndroidId,
                            permission_level = permission
                        )

                        viewModel.saveDataLocally(
                            context,
                            email = email?:"",
                            name = name,
                            phone = phone,
                            roll = roll,
                            branch = branch,
                            course = course
                        )
                        viewModel.insertProfile(profile = newSupabaseProfile, repo = repo)
                        onSaveClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp) // Made the button slightly taller for a modern feel
                ) {
                    Text("Save", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
