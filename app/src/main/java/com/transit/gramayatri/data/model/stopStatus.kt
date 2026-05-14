package com.transit.gramayatri.data.model

enum class StopState {
    PASSED,
    CURRENT,
    UPCOMING,
    UNKNOWN
}

data class StopUiState(
    val index: Int,
    val name: String,
    val state: StopState,
    val etaText: String,
    val reporterText: String,
    val isFirst: Boolean,
    val isLast: Boolean
)
