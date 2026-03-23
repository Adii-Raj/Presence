package com.application.presence.viewmodel.Factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.application.presence.repository.AuthRepository
import com.application.presence.repository.LocalUserRepository
import com.application.presence.viewmodel.SplashViewModel

class SplashViewModelFactory(
    private val repository: LocalUserRepository // Add whatever parameters SplashViewModel actually needs
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}