package com.application.presence.repository

import android.content.Context
import com.application.presence.data.SupabaseClientProvider
import com.application.presence.data.local.userDataStore
import com.application.presence.data.model.LocalProfile
import com.application.presence.data.local.UserKeysLocal
import kotlinx.coroutines.flow.first

class LocalUserRepository(private val context: Context) {

    suspend fun isUserDataSaved(): Boolean {
        val prefs = context.userDataStore.data.first()
        return (prefs[UserKeysLocal.EMAIL] ?: "").isNotEmpty()
    }

    suspend fun userProfile(): LocalProfile{
        val prefs = context.userDataStore.data.first()

        return LocalProfile(
            email = prefs[UserKeysLocal.EMAIL] ?: "",
            name = prefs[UserKeysLocal.NAME] ?: "",
            roll = prefs[UserKeysLocal.ROLL] ?: "",
            phone = prefs[UserKeysLocal.PHONE] ?: "",
            course = prefs[UserKeysLocal.COURSE] ?: "",
            branch = prefs[UserKeysLocal.BRANCH] ?: "",
            permission = prefs[UserKeysLocal.PERMISSION] ?: ""
        )
    }
}