package com.application.presence.data.state

sealed class ScannerSubmissionState{
    object Loading: ScannerSubmissionState()
    object Success: ScannerSubmissionState()
    data class Error(val message: String): ScannerSubmissionState()
    object Stopped: ScannerSubmissionState()
}