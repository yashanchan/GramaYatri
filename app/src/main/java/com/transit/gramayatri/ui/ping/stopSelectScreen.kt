package com.transit.gramayatri.ui.ping

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.transit.gramayatri.data.model.Routes
import com.transit.gramayatri.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun stopSelectScreen(
    busId: String,
    viewModel: PingViewModel,
    onBack: () -> Unit
) {
    val route = Routes.findById(busId) ?: run { onBack(); return }
    var selectedStopIndex by remember { mutableStateOf(-1) }
    var showConfirmArrived by remember { mutableStateOf(false) }
    var showConfirmCancel  by remember { mutableStateOf(false) }

    if (showConfirmArrived && selectedStopIndex >= 0) {
        AlertDialog(
            onDismissRequest = { showConfirmArrived = false },
            title   = { Text("Confirm arrival ping") },
            text    = {
                Text("Confirm that Bus $busId has arrived at " +
                        "${route.stops[selectedStopIndex]}?")
            },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmArrived = false
                    viewModel.submitPing(
                        busId     = busId,
                        stopIndex = selectedStopIndex,
                        stopName  = route.stops[selectedStopIndex]
                    )
                    onBack()
                }) { Text("Confirm", color = BrandOrange) }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmArrived = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showConfirmCancel) {
        AlertDialog(
            onDismissRequest = { showConfirmCancel = false },
            title   = { Text("Report cancellation?") },
            text    = {
                Text("Report Bus $busId as cancelled today? " +
                        "This will notify all passengers on this route.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmCancel = false
                    viewModel.submitCancellation(busId)
                }) { Text("Yes, report", color = CancelRed) }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmCancel = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Bus $busId", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            "${route.stops.first().lowercase()} → ${route.stops.last().lowercase()}",
                            fontSize = 12.sp,
                            color    = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text  = if (selectedStopIndex < 0) "No stop selected"
                    else "Selected: ${route.stops[selectedStopIndex]}",
                    fontSize = 12.sp,
                    color    = TextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Button(
                    onClick  = { if (selectedStopIndex >= 0) showConfirmArrived = true },
                    enabled  = selectedStopIndex >= 0,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor         = BrandOrange,
                        disabledContainerColor = BrandOrange.copy(alpha = 0.4f)
                    )
                ) {
                    Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Bus has arrived", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }

                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick  = { showConfirmCancel = true },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape    = RoundedCornerShape(12.dp),
                    border   = androidx.compose.foundation.BorderStroke(1.5.dp, CancelRed),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = CancelRed)
                ) {
                    Icon(Icons.Default.Cancel, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Bus has been cancelled", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
            }
        },
        containerColor = SurfaceGray
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .background(BrandOrangeBg, RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("👆", fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    "Tap a stop to select where you saw this bus.",
                    fontSize   = 13.sp,
                    color      = BrandOrange,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Card(
                modifier  = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                LazyColumn {
                    itemsIndexed(route.stops) { index, stopName ->
                        val isSelected = index == selectedStopIndex
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedStopIndex = index }
                                .background(
                                    if (isSelected) BrandOrangeBg
                                    else Color.Transparent
                                )
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(
                                        if (isSelected) BrandOrange else SurfaceGray,
                                        RoundedCornerShape(50)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text      = "${index + 1}",
                                    fontSize  = 11.sp,
                                    color     = if (isSelected) Color.White else TextSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text       = stopName,
                                fontSize   = 14.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold
                                else FontWeight.Normal,
                                color      = if (isSelected) BrandOrange else TextPrimary
                            )
                        }
                        if (index < route.stops.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color    = DividerColor
                            )
                        }
                    }
                }
            }
        }
    }
}