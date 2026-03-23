package com.application.presence.viewmodel

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.presence.data.local.userDataStore
import com.application.presence.data.model.AuthResponse
import com.application.presence.data.local.DeviceManager
import com.application.presence.data.model.LocalProfile
import com.application.presence.data.local.UserKeysLocal
import com.application.presence.data.model.Profile
import com.application.presence.repository.AuthRepository
import com.application.presence.repository.LocalUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository,
    private val deviceManager: DeviceManager,
    private val repo : LocalUserRepository,
    val context: Context
) : ViewModel() {
    private val _profile = MutableStateFlow<LocalProfile?>(null)
    val profile:StateFlow<LocalProfile?> = _profile

    private val _supabaseProfile = MutableStateFlow<Profile?>(null)
    val supabaseProfile: StateFlow<Profile?> = _supabaseProfile

    init {
        loadProfile()
    }
    //Loads Profile data if existed in _profile stateflow
    fun loadProfile(){
        viewModelScope.launch {
            val result = repo.userProfile()
            _profile.value = result
        }
    }

    fun getDeviceId():String{
        return deviceManager.getAndroidId()
    }

    private suspend fun saveUserToLocal(
        context: Context,
        email: String,
        name: String,
        phone: String,
        roll: String,
        branch: String,
        course: String,
        permission: String
    ){
        context.userDataStore.edit { prefs ->
            prefs[UserKeysLocal.EMAIL] = email
            prefs[UserKeysLocal.NAME] = name
            prefs[UserKeysLocal.PHONE] = phone
            prefs[UserKeysLocal.ROLL] = roll
            prefs[UserKeysLocal.BRANCH] = branch
            prefs[UserKeysLocal.COURSE] = course
            prefs[UserKeysLocal.PERMISSION] = permission
        }
    }
    fun saveDataLocally(
        context: Context,
        email: String,
        name: String,
        phone: String,
        roll: String,
        branch: String,
        course: String,
        permission: String = "user"
    ){
        viewModelScope.launch {
            saveUserToLocal(
                context,
                email = email,
                name = name,
                phone = phone,
                roll = roll,
                branch = branch,
                course = course,
                permission = permission
            )
        }
    }

    private val _authState = MutableStateFlow<AuthResponse?>(null)
    val authState: StateFlow<AuthResponse?> = _authState

    fun loginWithGoogle() {
        repository.signInWithGoogle()
            .onEach {
                _authState.value = it
            }
            .launchIn(viewModelScope)
    }


    fun insertProfile(profile: Profile, repo: AuthRepository){
        viewModelScope.launch {
            repo.InsertProfile(profile)
        }
    }

    fun SupabaseProfile(){
        viewModelScope.launch {
            val profile = repository.supabaseProfile()
            _supabaseProfile.value = profile

            saveUserToLocal(
                context,
                profile?.gmail_id ?: "",
                profile?.name ?: "",
                profile?.phone ?: "",
                profile?.roll?:"",
                profile?.branch?:"",
                profile?.course?:"",
                profile?.permission_level?: "user"
            )
        }
    }

    private val _returnedGmailId = MutableStateFlow("")
    val returnedGmailId: StateFlow<String> = _returnedGmailId

    fun getReturnedAndorid(id: String?){
        viewModelScope.launch {
            _returnedGmailId.value = repository.AndroidIdReturnedByGmail(id?:"")
        }
    }

}