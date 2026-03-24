package com.application.presence.data.model

data class StudentAttendance(
    val id: String, // The unique attendance record ID from Supabase
    val name: String,
    val rollNumber: String,
    val phone: String,
    val section: String,
    val semester: String
)