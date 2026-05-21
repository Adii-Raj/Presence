package com.application.presence.Screen

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.presence.Screen.Components.DetailScreen
import com.application.presence.Screen.Components.MapPicker
import com.application.presence.data.model.EventDataClass
import com.application.presence.data.state.EventInsertState
import com.application.presence.viewmodel.AddEventViewModel


@Composable
fun AddEventScreenUI(
    eventToEdit: EventDataClass? = null,
    onSaveClick: (EventDataClass) -> Unit,
    onDeleteClick: (String) -> Unit = {},
    onNavigate:() -> Unit
) {
    val viewModel: AddEventViewModel = viewModel()
    val IsLocationSaved = viewModel.isLocationSaved.collectAsStateWithLifecycle().value
    val insertState by viewModel.insertState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // If we are editing, we might want to skip the MapPicker initially if coordinates exist,
    // but the current flow requires IsLocationSaved to be true for DetailScreen.
    // Let's force IsLocationSaved to true if we have an eventToEdit with coordinates.
    LaunchedEffect(eventToEdit) {
        if (eventToEdit?.coordinates != null) {
            viewModel.updateLocationStatus(true)
        }
    }

    LaunchedEffect(insertState) {
        when(insertState){
            is EventInsertState.IsSuccess -> {
                val message = if (eventToEdit == null) "Event Added Successfully" else "Event Updated Successfully"
                Toast.makeText(
                    context,
                    message,
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.resetInsertState()
                onNavigate()
            }
            is EventInsertState.Error -> {
                val errorMsg = (insertState as EventInsertState.Error).message
                Toast.makeText(
                    context,
                    "Failed: $errorMsg",
                    Toast.LENGTH_SHORT
                ).show()
            } else -> {/*Don't do anything*/}
        }
    }

    if(IsLocationSaved){
        DetailScreen(
            event = eventToEdit,
            onSaveClick = { eventDataClass ->
                onSaveClick(eventDataClass)
            },
            onDeleteClick = { id ->
                onDeleteClick(id)
            }
        )
    }
    else{
        MapPicker()
    }
}


@Preview(showBackground = true)
@Composable
fun Showing(){
    AddEventScreenUI(onSaveClick = {}, onNavigate = {})
}
