package com.transit.gramayatri.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.transit.gramayatri.data.model.*
import com.transit.gramayatri.data.repository.busRepo
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class BusCardUi(
    val busId: String,
    val displayName: String,
    val routeSummary: String,
    val etaPillText: String,
    val reporterText: String
)

class searchViewModel(private val busRepo: busRepo) : ViewModel() {
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()
    private val _busCards = MutableStateFlow<List<BusCardUi>>(emptyList())
    val busCards: StateFlow<List<BusCardUi>> = _busCards.asStateFlow()
    private val _stopStates = MutableStateFlow<List<StopUiState>>(emptyList())
    val stopStates: StateFlow<List<StopUiState>> = _stopStates.asStateFlow()
    private val _delayBanner = MutableStateFlow("")
    val delayBanner: StateFlow<String> = _delayBanner.asStateFlow()

    init {
        loadAllBusCards()
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        filterBusCards(newQuery)
    }
    private fun loadAllBusCards() {
        viewModelScope.launch {
            val cards = Routes.all.map { route ->
                BusCardUi(
                    busId       = route.busId,
                    displayName = route.busId,
                    routeSummary = "${route.stops.first().lowercase()} → ${route.stops.last().lowercase()}",
                    etaPillText  = "No recent pings",
                    reporterText = ""
                )
            }
            _busCards.value = cards
        }
    }

    private fun filterBusCards(query: String) {
        val all = Routes.all
        val filtered = if (query.isBlank()) all
        else all.filter { route ->
            route.busId.contains(query, ignoreCase = true) ||
                    route.stops.any { it.contains(query, ignoreCase = true) }
        }
        _busCards.value = filtered.map { route ->
            BusCardUi(
                busId        = route.busId,
                displayName  = route.busId,
                routeSummary = "${route.stops.first().lowercase()} → ${route.stops.last().lowercase()}",
                etaPillText  = "Loading...",
                reporterText = ""
            )
        }
    }
    fun loadTimeline(busId: String) {
        val route = Routes.findById(busId) ?: return

        viewModelScope.launch {
            busRepo.listenToPings(busId).collect { pings ->
                val stops = buildTimeline(route, pings)
                _stopStates.value = stops
                _delayBanner.value = generatePlaceholderBanner(pings, route)
            }
        }
    }
    private fun buildTimeline(route: busRoute, pings: List<ping>): List<StopUiState> {
        val latestPing = pings.filter { it.type == "arrived" }
            .maxByOrNull { it.timestamp }

        val now = System.currentTimeMillis()
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        return route.stops.mapIndexed { index, stopName ->
            when {
                latestPing == null -> StopUiState(
                    index        = index,
                    name         = stopName,
                    state        = StopState.UNKNOWN,
                    etaText      = "No report yet",
                    reporterText = "",
                    isFirst      = index == 0,
                    isLast       = index == route.stops.lastIndex
                )
                index < latestPing.stopIndex -> StopUiState(
                    index        = index,
                    name         = stopName,
                    state        = StopState.PASSED,
                    etaText      = timeFormat.format(Date(
                        latestPing.timestamp - minutesBefore(route, index, latestPing.stopIndex)
                    )),
                    reporterText = if (index == latestPing.stopIndex - 1)
                        reporterLine(latestPing, now) else "",
                    isFirst      = index == 0,
                    isLast       = index == route.stops.lastIndex
                )
                index == latestPing.stopIndex -> StopUiState(
                    index        = index,
                    name         = stopName,
                    state        = StopState.CURRENT,
                    etaText      = timeFormat.format(Date(latestPing.timestamp)),
                    reporterText = reporterLine(latestPing, now),
                    isFirst      = index == 0,
                    isLast       = index == route.stops.lastIndex
                )
                else -> {
                    val minutesAhead = (latestPing.stopIndex until index).sumOf { i ->
                        route.avgTimesMinutes.getOrElse(i) { 5 }
                    }
                    val etaMillis = latestPing.timestamp + minutesAhead * 60 * 1000L
                    val minutesFromNow = ((etaMillis - now) / 60000).toInt()

                    StopUiState(
                        index        = index,
                        name         = stopName,
                        state        = StopState.UPCOMING,
                        etaText      = if (minutesFromNow in 1..60)
                            "Arriving in ~$minutesFromNow min"
                        else
                            timeFormat.format(Date(etaMillis)) + " (est.)",
                        reporterText = "No report yet",
                        isFirst      = index == 0,
                        isLast       = index == route.stops.lastIndex
                    )
                }
            }
        }
    }
    private fun minutesBefore(route: busRoute, fromIndex: Int, toIndex: Int): Long {
        return (fromIndex until toIndex).sumOf { i ->
            route.avgTimesMinutes.getOrElse(i) { 5 }
        } * 60 * 1000L
    }
    private fun reporterLine(ping: ping, now: Long): String {
        val minutesAgo = ((now - ping.timestamp) / 60000).toInt()
        val timeAgo = when {
            minutesAgo < 1  -> "just now"
            minutesAgo == 1 -> "1 min ago"
            minutesAgo < 60 -> "$minutesAgo min ago"
            else            -> "over 1 hr ago"
        }
        return "Reported by ${ping.userName} · $timeAgo"
    }
    private fun generatePlaceholderBanner(pings: List<ping>, route: busRoute): String {
        val latest = pings.filter { it.type == "arrived" }.maxByOrNull { it.timestamp }
            ?: return ""
        val minutesAgo = ((System.currentTimeMillis() - latest.timestamp) / 60000).toInt()
        return if (minutesAgo < 30)
            "Bus is running — last seen at ${latest.stopName} · ${minutesAgo} min ago"
        else
            "No recent updates for this route"
    }
}