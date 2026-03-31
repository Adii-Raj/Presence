package com.application.presence.data.model

sealed class LocationState {
    object IsLoading : LocationState()
    data class Error(val message:String) : LocationState()
    data class IsScucess(val latitude:String, val longitude: String) : LocationState()
}