package com.transit.gramayatri.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_pings")
data class cachedPingEntity(
    @PrimaryKey
    val pingId: String,
    val busId: String,
    val stopIndex: Int,
    val stopName: String,
    val type: String,
    val userName: String,
    val timestamp: Long
)