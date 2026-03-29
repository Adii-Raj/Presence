package com.application.presence.data.model

import kotlinx.serialization.Serializable

@Serializable
data class EventDataClass(
    val id: String? = null,
    val Event_Name:String,
    val Event_Date:String?,
    val Event_Time:String?,
    val Event_Location:String?,
    val Event_Description:String?,
    val Event_Organiser: List<OrganizerInput>?,
    val Event_Image:String?,
    val Event_Note: String?,
    val coordinates: String? = null,
    val coordinates_radius: Float? = null
)

@Serializable
data class OrganizerInput(
    val name: String = "",
    val phone: String = ""
)
