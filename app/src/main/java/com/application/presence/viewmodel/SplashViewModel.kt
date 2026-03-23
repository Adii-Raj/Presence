package com.application.presence.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.presence.data.SupabaseClientProvider
import com.application.presence.data.local.userDataStore
import com.application.presence.data.model.LocalProfile
import com.application.presence.data.local.UserKeysLocal
import com.application.presence.data.model.Profile
import com.application.presence.data.state.SplashState
import com.application.presence.repository.LocalUserRepository
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashViewModel(
    private val repo: LocalUserRepository
): ViewModel() {
    private val _splashState = MutableStateFlow<SplashState>(SplashState.Loading)
    val splashState: StateFlow<SplashState> = _splashState.asStateFlow()

    init {
        checkAuthenticationState()
    }

    private fun checkAuthenticationState() {
        viewModelScope.launch {
            val supabase = SupabaseClientProvider.client
            val profile = supabase.auth.currentUserOrNull()

            val isLoggedIn = profile!=null

            //val preference = context.userDataStore.data.first()  --> We can use this instead of prefs
            //val email2 = preference[UserKeysLocal.EMAIL]


            val isDataSaved = repo.isUserDataSaved()

            if (isLoggedIn) {
                if(isDataSaved){
                    _splashState.value = SplashState.NavigateToHome
                }else{
                    _splashState.value = SplashState.NavigateToLogin
                }
            } else {
                _splashState.value = SplashState.NavigateToLogin
            }
        }
    }
}