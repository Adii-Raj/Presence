package com.application.presence.data.state

sealed class SplashState {
    object Loading : SplashState()
    object NavigateToLogin : SplashState()
    object NavigateToHome : SplashState()
    object NavigateToDetail : SplashState()
}