package com.application.presence.data.state

import com.application.presence.data.model.EventDataClass

sealed class EventState {
    object Loading: EventState()
    data class Success(val data: List<EventDataClass>) : EventState()
    data class Error(val message: String) : EventState()
}