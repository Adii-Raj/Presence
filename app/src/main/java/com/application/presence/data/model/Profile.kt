package com.application.presence.data.model

import kotlinx.serialization.Serializable


@Serializable
data class Profile(
    val id:String? = null,
    val name: String,
    val gmail_id: String,
    val phone: String?,
    val roll : String?,
    val course: String?,
    val branch : String?,
    val device_fingerprint:String?,
    val permission_level: String?
)