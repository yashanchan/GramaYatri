package com.transit.gramayatri.ui.ping

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.transit.gramayatri.data.model.Routes
import com.transit.gramayatri.data.repository.busRepo
import com.transit.gramayatri.data.repository.userRepo
import com.transit.gramayatri.ui.theme.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info

@Composable
fun pingScreen(
    userRepository: userRepo,
    busRepository: busRepo
) {
    val vm: PingViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PingViewModel(busRepository, userRepository) as T
            }
        }
    )

    val uiState    by vm.uiState.collectAsState()
    val busIdQuery by vm.busIdQuery.collectAsState()
    var showStopSelect by remember { mutableStateOf(false) }
    var selectedBusId  by remember { mutableStateOf("") }

    if (showStopSelect && selectedBusId.isNotEmpty()) {
        stopSelectScreen(
            busId      = selectedBusId,
            viewModel  = vm,
            onBack     = {
                showStopSelect = false
                vm.reset()
            }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceGray)
    ) {
        // ── Header ─────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                "Ping / Cancellation",
                style      = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Help others by reporting bus status",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(Modifier.height(10.dp))

            // Bus ID search bar
            OutlinedTextField(
                value         = busIdQuery,
                onValueChange = vm::onBusIdQueryChange,
                placeholder   = { Text("Enter bus ID (e.g. 53A)") },
                leadingIcon   = {
                    Icon(Icons.Default.DirectionsBus, null, tint = TextSecondary)
                },
                trailingIcon  = {
                    IconButton(onClick = { vm.searchBus() }) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Search",
                            tint               = BrandOrange
                        )
                    }
                },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    imeAction      = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(onSearch = { vm.searchBus() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor      = BrandOrange,
                    unfocusedBorderColor    = DividerColor,
                    focusedContainerColor   = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
        }
        when (val state = uiState) {
            is PingUiState.Idle -> {
                EmptyPingState()
            }

            is PingUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandOrange)
                }
            }
            is PingUiState.BusFound -> {
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable {
                            selectedBusId  = state.busId
                            showStopSelect = true
                        },
                    shape     = RoundedCornerShape(14.dp),
                    colors    = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(BrandOrangeBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.DirectionsBus, null,
                                tint = BrandOrange, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "Bus ${state.busId}",
                                fontWeight = FontWeight.Bold,
                                fontSize   = 16.sp
                            )
                            Text(
                                state.routeSummary,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    "Tap the card to select a stop and report.",
                    modifier  = Modifier.padding(horizontal = 20.dp),
                    fontSize  = 13.sp,
                    color     = TextSecondary
                )
            }

            is PingUiState.NotFound -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No bus with ID '${state.query}' found.\nPlease check and try again.",
                        textAlign = TextAlign.Center,
                        color     = TextSecondary
                    )
                }
            }

            is PingUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SurfaceGray)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Success circle
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(EtaPillGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint               = EtaPillGreenText,
                            modifier           = Modifier.size(56.dp)
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Text(
                        text       = "Ping Submitted!",
                        style      = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color      = TextPrimary,
                        textAlign  = TextAlign.Center
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text      = state.message,
                        style     = MaterialTheme.typography.bodyMedium,
                        color     = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(8.dp))

                    // Info box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(EtaPillGreen, RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint     = EtaPillGreenText,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text  = "All passengers on this route have been updated with the new ETA.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = EtaPillGreenText
                        )
                    }

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = { vm.reset(); showStopSelect = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape  = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                    ) {
                        Text(
                            "Done",
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 16.sp
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { vm.reset(); showStopSelect = false },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape   = RoundedCornerShape(12.dp),
                        border  = BorderStroke(1.5.dp, BrandOrange),
                        colors  = ButtonDefaults.outlinedButtonColors(contentColor = BrandOrange)
                    ) {
                        Text("Report Another Bus", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    }
                }
            }
            is PingUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("❌", fontSize = 40.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(state.message, textAlign = TextAlign.Center, color = CancelRed)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { vm.reset() },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                        ) { Text("Try again") }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyPingState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Campaign,
            contentDescription = null,
            tint     = BrandOrange,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Search for a bus to report its status",
            style      = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color      = TextPrimary,
            textAlign  = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Enter the bus ID printed on the front of the bus, then tap the stop where you saw it to report an arrival or cancellation.",
            style     = MaterialTheme.typography.bodyMedium,
            color     = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}