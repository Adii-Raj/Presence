package com.application.presence.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.presence.data.state.EventState
import com.application.presence.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventViewModel(
    private val repository: HomeRepository
) : ViewModel() {
    init {
        getEvent()
    }

    private val _eventState = MutableStateFlow<EventState>(EventState.Loading)
    val eventState: StateFlow<EventState> = _eventState

    fun getEvent(){
        viewModelScope.launch {
            _eventState.value = EventState.Loading
            try {
                val list = repository.getEvent()
                _eventState.value = EventState.Success(list)
            } catch (e: Exception) {
                _eventState.value = EventState.Error(e.message ?: "Error Loading Event")
            }
        }
    }
}