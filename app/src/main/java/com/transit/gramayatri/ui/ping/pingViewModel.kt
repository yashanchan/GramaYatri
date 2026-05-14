package com.transit.gramayatri.ui.ping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.transit.gramayatri.data.model.ping
import com.transit.gramayatri.data.model.Routes
import com.transit.gramayatri.data.repository.busRepo
import com.transit.gramayatri.data.repository.userRepo
import kotlinx.coroutines.flow.*

sealed class PingUiState {
    object Idle : PingUiState()
    object Loading : PingUiState()
    data class BusFound(val busId: String, val routeSummary: String) : PingUiState()
    data class NotFound(val query: String) : PingUiState()
    data class Success(val message: String) : PingUiState()
    data class Error(val message: String) : PingUiState()
}

class PingViewModel(
    private val busRepo: busRepo,
    private val userRepo: userRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow<PingUiState>(PingUiState.Idle)
    val uiState: StateFlow<PingUiState> = _uiState.asStateFlow()

    private val _busIdQuery = MutableStateFlow("")
    val busIdQuery: StateFlow<String> = _busIdQuery.asStateFlow()

    fun onBusIdQueryChange(q: String) { _busIdQuery.value = q }

    fun searchBus() {
        val q = _busIdQuery.value.trim()
        if (q.isBlank()) return
        val route = Routes.findById(q)
        _uiState.value = if (route != null) {
            PingUiState.BusFound(
                busId        = route.busId,
                routeSummary = "${route.stops.first()} → ${route.stops.last()}"
            )
        } else {
            PingUiState.NotFound(q)
        }
    }

    fun submitPing(busId: String, stopIndex: Int, stopName: String) {
        val userName = userRepo.getUserName().ifBlank { "Anonymous" }
        val ping = ping(
            busId     = busId,
            stopIndex = stopIndex,
            stopName  = stopName,
            type      = "arrived",
            userName  = userName,
            timestamp = System.currentTimeMillis()
        )
        _uiState.value = PingUiState.Loading
        busRepo.submitPing(
            ping      = ping,
            onSuccess = {
                _uiState.value = PingUiState.Success("Thanks ${userName}! Ping submitted for $stopName.")
            },
            onError = { err ->
                _uiState.value = PingUiState.Error(err)
            }
        )
    }

    fun submitCancellation(busId: String) {
        val userName = userRepo.getUserName().ifBlank { "Anonymous" }
        busRepo.submitCancellation(
            busId    = busId,
            userName = userName,
            onSuccess = {
                _uiState.value = PingUiState.Success("Bus $busId reported as cancelled.")
            },
            onError = { err ->
                _uiState.value = PingUiState.Error(err)
            }
        )
    }

    fun reset() { _uiState.value = PingUiState.Idle }
}