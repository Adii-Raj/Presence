package com.application.presence.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String,
    val gmailid: String? = null
)