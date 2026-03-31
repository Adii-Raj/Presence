package com.application.presence.data.state

import javax.crypto.SecretKey

sealed class ScannerSubmissionState{
    object Loading: ScannerSubmissionState()
    object Success: ScannerSubmissionState()
    data class Error(val message: String?): ScannerSubmissionState()
    object Stopped: ScannerSubmissionState()
}