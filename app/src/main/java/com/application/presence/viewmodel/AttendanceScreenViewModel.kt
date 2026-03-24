package com.application.presence.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class AttendanceScreenViewModel: ViewModel() {
    // In your ViewModel:
    var selectedStudentIds = MutableStateFlow<Set<String>>(emptySet())

    // When a checkbox is clicked:
    fun toggleStudentSelection(studentId: String, isSelected: Boolean) {
        val currentSet = selectedStudentIds.value.toMutableSet()
        if (isSelected) {
            currentSet.add(studentId)
        } else {
            currentSet.remove(studentId)
        }
        selectedStudentIds.value = currentSet
    }

    // When delete is clicked:
    fun deleteSelectedStudents() {
        val idsToDelete = selectedStudentIds.value.toList()
        // 1. Call Supabase to delete these rows from the attendance table
        // supabaseClient.from("attendance").delete { filter { isIn("id", idsToDelete) } }

        // 2. Clear the selection
        selectedStudentIds.value = emptySet()

        // 3. Refresh the list
    }
}