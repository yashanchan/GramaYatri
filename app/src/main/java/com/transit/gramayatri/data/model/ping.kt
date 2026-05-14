package com.transit.gramayatri.data.model

data class ping(
    val pingId: String = "",
    val busId: String = "",
    val stopIndex: Int = 0,
    val stopName: String = "",
    val type: String = "arrived",
    val userName: String = "",
    val timestamp: Long = 0L
)