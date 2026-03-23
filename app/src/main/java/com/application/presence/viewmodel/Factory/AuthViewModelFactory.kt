package com.application.presence.viewmodel.Factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.application.presence.data.local.DeviceManager
import com.application.presence.repository.AuthRepository
import com.application.presence.repository.LocalUserRepository
import com.application.presence.viewmodel.AuthViewModel

class AuthViewModelFactory(
    private val repository: AuthRepository,
    private val deviceManager: DeviceManager,
    private val localUserRepository: LocalUserRepository,
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(repository,deviceManager, localUserRepository, context) as T
    }
}