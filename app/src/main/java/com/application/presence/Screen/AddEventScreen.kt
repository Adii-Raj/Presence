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
import com.application.presence.data.model.OrganizerInput
import com.application.presence.data.state.EventInsertState
import com.application.presence.viewmodel.AddEventViewModel


@Composable
fun AddEventScreenUI(
    onSaveClick: (EventDataClass) -> Unit,
    onNavigate:() -> Unit
) {
    val viewModel: AddEventViewModel = viewModel()
    val IsLocationSaved = viewModel.isLocationSaved.collectAsStateWithLifecycle().value
    val insertState by viewModel.insertState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(insertState) {
        when(insertState){
            is EventInsertState.IsSuccess -> {
                Toast.makeText(
                    context,
                    "Event Added Successfully",
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
    AddEventScreenUI({}, {})
}