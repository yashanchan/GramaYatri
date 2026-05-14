package com.transit.gramayatri.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface pingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPing(ping: cachedPingEntity)

    @Query("SELECT * FROM cached_pings WHERE busId = :busId ORDER BY timestamp DESC LIMIT 1")
    fun getLatestPingForBus(busId: String): Flow<cachedPingEntity?>

    @Query("SELECT * FROM cached_pings WHERE busId = :busId ORDER BY timestamp DESC")
    fun getAllPingsForBus(busId: String): Flow<List<cachedPingEntity>>

    @Query("DELETE FROM cached_pings WHERE timestamp < :cutoffTimestamp")
    suspend fun deleteOldPings(cutoffTimestamp: Long)
}