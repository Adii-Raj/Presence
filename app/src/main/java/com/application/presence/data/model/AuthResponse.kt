package com.application.presence.data.model


sealed interface AuthResponse {
    data object Loading : AuthResponse
    data class Success(val isNewUser: Boolean, val email: String?) : AuthResponse
    data class Error(val message: String?) : AuthResponse
}