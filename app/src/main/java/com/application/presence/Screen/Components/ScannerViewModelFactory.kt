package com.application.presence.Screen.Components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.application.presence.viewmodel.ScannerViewModel

// This factory tells Android exactly how to build your ScannerViewModel
// by passing in the onSubmissionSuccess function.
class ScannerViewModelFactory(
    private val onSubmissionSuccess: () -> Unit
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScannerViewModel::class.java)) {
            return ScannerViewModel(onSubmissionSuccess) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}