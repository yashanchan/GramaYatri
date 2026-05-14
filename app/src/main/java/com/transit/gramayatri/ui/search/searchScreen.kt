package com.transit.gramayatri.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import com.transit.gramayatri.data.repository.busRepo
import com.transit.gramayatri.ui.theme.*

@Composable
fun searchScreen(
    busRepository: busRepo,
    onBusSelected: (String) -> Unit
) {
    val vm: searchViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return searchViewModel(busRepository) as T
            }
        }
    )

    val query     by vm.query.collectAsState()
    val busCards  by vm.busCards.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceGray)
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                text       = "Bus Search",
                style      = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary
            )
            Text(
                text  = "Live, community-reported buses",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value         = query,
                onValueChange = vm::onQueryChange,
                placeholder   = { Text("Enter destination (e.g. Simhachalam)") },
                leadingIcon   = {
                    Icon(Icons.Default.Search, contentDescription = null,
                        tint = TextSecondary)
                },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = BrandOrange,
                    unfocusedBorderColor = DividerColor,
                    focusedContainerColor   = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(busCards) { bus ->
                BusSearchCard(bus = bus, onClick = { onBusSelected(bus.busId) })
            }
        }
    }
}

@Composable
fun BusSearchCard(bus: BusCardUi, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                Icon(
                    imageVector        = Icons.Default.DirectionsBus,
                    contentDescription = null,
                    tint               = BrandOrange,
                    modifier           = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text       = "Bus ${bus.busId}",
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color      = TextPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                    if (bus.etaPillText != "Loading..." && bus.etaPillText != "No recent pings") {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(EtaPillGreen)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.AccessTime, null,
                                    tint     = EtaPillGreenText,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(Modifier.width(3.dp))
                                Text(
                                    text      = bus.etaPillText,
                                    fontSize  = 11.sp,
                                    color     = EtaPillGreenText,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector        = Icons.Default.DirectionsBus,
                        contentDescription = null,
                        tint               = TextSecondary,
                        modifier           = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text     = bus.routeSummary,
                        style    = MaterialTheme.typography.bodyMedium,
                        color    = TextSecondary,
                        maxLines = 1
                    )
                }
                if (bus.reporterText.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Person, null,
                            tint     = TextHint,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text  = bus.reporterText,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextHint
                        )
                    }
                }
            }
        }
    }
}