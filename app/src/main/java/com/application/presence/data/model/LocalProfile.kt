package com.application.presence.data.model

data class LocalProfile(
    val email: String,
    val name: String,
    val roll: String,
    val phone: String,
    val course: String,
    val branch: String,
    val permission: String = "user"
)