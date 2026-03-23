package com.application.presence.data.local

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

//It is a helper class
class DeviceManager(private val context: Context) {
    @SuppressLint("HardwareIds")
    fun getAndroidId(): String{
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "UNKNOWN"
    }
}