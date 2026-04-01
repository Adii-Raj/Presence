package com.application.presence.viewmodel

import android.app.Application
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.presence.data.model.KeysDataClass
import com.application.presence.data.state.EventState
import com.application.presence.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventViewModel(
    application: Application,
    private val repository: HomeRepository
) : AndroidViewModel(application) {

    private val _eventState = MutableStateFlow<EventState>(EventState.Loading)
    val eventState: StateFlow<EventState> = _eventState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing


    init {
        getEvent()
    }

    fun getEvent(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                _isRefreshing.value = true // Turn on the pull-to-refresh spinner
            } else {
                _eventState.value = EventState.Loading // Turn on the full-screen spinner
            }

            try {
                val list = repository.getEvent()
                _eventState.value = EventState.Success(list)
            } catch (e: Exception) {
                _eventState.value = EventState.Error(e.message ?: "Error Loading Event")
            } finally {
                // 2. This guarantees the spinner turns off no matter what!
                _isRefreshing.value = false
            }
        }
    }

    private val _key = MutableStateFlow<KeysDataClass?>(null)
    val key: StateFlow<KeysDataClass?> =_key

    fun getUniqueAndSecret(id: String){
        viewModelScope.launch {
            _key.value = repository.getUniqueAndSecret(id)
        }
    }
}