package com.application.presence.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceRequest(
    val uniqueTag: String,
    val scannedCode: String,
    val studentRoll: String,
    val userLatitude: Double,
    val userLongitude: Double
)