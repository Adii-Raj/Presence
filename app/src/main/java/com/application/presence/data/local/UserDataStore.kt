package com.application.presence.data.local

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.userDataStore by preferencesDataStore(name = "user_data")