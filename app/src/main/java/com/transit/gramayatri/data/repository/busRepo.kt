package com.transit.gramayatri.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.transit.gramayatri.data.local.appDatabase
import com.transit.gramayatri.data.local.cachedPingEntity
import com.transit.gramayatri.data.model.ping
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class busRepo(private val db: appDatabase) {
    private val firebaseDb = FirebaseDatabase.getInstance()
    fun submitPing(ping: ping, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val ref = firebaseDb
            .getReference("routes")
            .child(ping.busId)
            .child("pings")
            .push()

        val pingWithId = ping.copy(pingId = ref.key ?: "")

        ref.setValue(pingWithId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Unknown error") }
    }

    fun submitCancellation(
        busId: String,
        userName: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val ref = firebaseDb
            .getReference("routes")
            .child(busId)
            .child("cancellation")

        val data = mapOf(
            "cancelled" to true,
            "reportedBy" to userName,
            "timestamp" to System.currentTimeMillis()
        )

        ref.setValue(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Unknown error") }
    }

    fun listenToPings(busId: String): Flow<List<ping>> = callbackFlow {
        val ref = firebaseDb
            .getReference("routes")
            .child(busId)
            .child("pings")
            .orderByChild("timestamp")
            .startAt((System.currentTimeMillis() - 6 * 60 * 60 * 1000).toDouble())

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pings = mutableListOf<ping>()
                for (child in snapshot.children) {
                    val ping = child.getValue(ping::class.java)
                    if (ping != null) pings.add(ping)
                }
                launch {
                    pings.forEach { ping ->
                        db.pingDao().insertPing(
                            cachedPingEntity(
                                pingId    = ping.pingId,
                                busId     = ping.busId,
                                stopIndex = ping.stopIndex,
                                stopName  = ping.stopName,
                                type      = ping.type,
                                userName  = ping.userName,
                                timestamp = ping.timestamp
                            )
                        )
                    }
                }
                trySend(pings.sortedByDescending { it.timestamp })
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
    fun getCachedPings(busId: String): Flow<List<cachedPingEntity>> =
        db.pingDao().getAllPingsForBus(busId)
}