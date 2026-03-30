package com.application.presence.data.model

import kotlinx.serialization.Serializable

@Serializable
data class KeysDataClass(
    val secret_key:String?,
    val unique_tag: String?
)