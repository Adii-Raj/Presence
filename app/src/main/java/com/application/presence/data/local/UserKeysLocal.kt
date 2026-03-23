package com.application.presence.data.local

import androidx.datastore.preferences.core.stringPreferencesKey

object UserKeysLocal{
    val EMAIL = stringPreferencesKey("email")
    val NAME = stringPreferencesKey("name")
    val PHONE = stringPreferencesKey("phone")
    val ROLL = stringPreferencesKey("roll")
    val BRANCH = stringPreferencesKey("branch")
    val COURSE = stringPreferencesKey("course")
    val PERMISSION = stringPreferencesKey("permission")
}