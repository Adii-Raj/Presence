package com.application.presence.data.state

sealed class EventInsertState {
    object Idle: EventInsertState()
    object IsLoading: EventInsertState()
    object IsSuccess: EventInsertState()
    data class Error(val message:String): EventInsertState()
}