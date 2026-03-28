package com.application.presence.Screen

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.presence.Screen.Components.DetailScreen
import com.application.presence.Screen.Components.MapPicker
import com.application.presence.data.model.EventDataClass
import com.application.presence.data.model.OrganizerInput
import com.application.presence.viewmodel.AddEventViewModel


@Composable
fun AddEventScreenUI(
    onSaveClick: (EventDataClass) -> Unit
) {
    val viewModel: AddEventViewModel = viewModel()
    val IsLocationSaved = viewModel.isLocationSaved.collectAsStateWithLifecycle().value
    if(IsLocationSaved){
        DetailScreen { eventDataClass ->
            onSaveClick(eventDataClass) }
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