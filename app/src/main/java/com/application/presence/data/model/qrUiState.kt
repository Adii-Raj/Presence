package com.application.presence.data.model

data class QrUiState(
    val qrPayload: String = "",       // e.g., "A7B2X9Y4:849201"
    val secondsRemaining: Long = 10L, // For the progress bar
    val isLoading: Boolean = true     // True until the first code is generated
)
