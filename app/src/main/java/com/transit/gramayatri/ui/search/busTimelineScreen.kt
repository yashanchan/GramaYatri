package com.transit.gramayatri.ui.search

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.transit.gramayatri.data.model.busRoute
import com.transit.gramayatri.data.model.Routes
import com.transit.gramayatri.data.model.StopState
import com.transit.gramayatri.data.model.StopUiState
import com.transit.gramayatri.data.repository.busRepo
import com.transit.gramayatri.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun busTimelineScreen(
    busId: String,
    busRepository: busRepo,
    onBack: () -> Unit
) {
    val vm: searchViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return searchViewModel(busRepository) as T
            }
        }
    )

    val stopStates  by vm.stopStates.collectAsState()
    val delayBanner by vm.delayBanner.collectAsState()
    val route: busRoute? = remember(busId) { Routes.findById(busId) }

    // Start listening to live pings for this bus
    LaunchedEffect(busId) {
        vm.loadTimeline(busId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text       = "Bus $busId",
                            fontWeight = FontWeight.Bold,
                            fontSize   = 18.sp
                        )
                        if (route != null) {
                            Text(
                                text      = "${route.stops.first().lowercase()} → ${route.stops.last().lowercase()}",
                                fontSize  = 12.sp,
                                color     = TextSecondary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = SurfaceGray
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            if (delayBanner.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .background(BrandOrangeBg, RoundedCornerShape(10.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint     = BrandOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text      = delayBanner,
                            fontSize  = 13.sp,
                            color     = BrandOrange,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            if (stopStates.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = BrandOrange)
                    }
                }
            } else {
                item {
                    Card(
                        modifier  = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        shape     = RoundedCornerShape(16.dp),
                        colors    = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            stopStates.forEach { stop ->
                                StopTimelineRow(stop = stop)
                            }
                        }
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LegendItem(color = StopPassed, label = "Passed stop")
                    LegendItem(color = StopUpcoming, label = "Upcoming stop")
                }
            }
        }
    }
}

@Composable
fun StopTimelineRow(stop: StopUiState) {
    val dotColor = when (stop.state) {
        StopState.PASSED  -> StopPassed
        StopState.CURRENT -> StopCurrent
        StopState.UPCOMING, StopState.UNKNOWN -> StopUpcoming
    }
    val isCurrent = stop.state == StopState.CURRENT

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .width(32.dp)
                .height(if (isCurrent) 72.dp else 60.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerX = size.width / 2
                val dotY    = 20f
                val dotRadius = if (isCurrent) 18f else 9f
                if (!stop.isFirst) {
                    drawLine(
                        color       = if (stop.state == StopState.PASSED ||
                            stop.state == StopState.CURRENT)
                            StopPassed else TimelineLine,
                        start       = Offset(centerX, 0f),
                        end         = Offset(centerX, dotY - dotRadius),
                        strokeWidth = 3f
                    )
                }
                if (!stop.isLast) {
                    drawLine(
                        color       = if (stop.state == StopState.PASSED)
                            StopPassed else TimelineLine,
                        start       = Offset(centerX, dotY + dotRadius),
                        end         = Offset(centerX, size.height),
                        strokeWidth = 3f
                    )
                }
                if (isCurrent) {
                    drawCircle(
                        color  = dotColor.copy(alpha = 0.2f),
                        radius = dotRadius,
                        center = Offset(centerX, dotY)
                    )
                }
                drawCircle(
                    color  = dotColor,
                    radius = if (isCurrent) 10f else dotRadius,
                    center = Offset(centerX, dotY)
                )
                if (isCurrent) {
                    drawCircle(
                        color  = Color.White,
                        radius = 5f,
                        center = Offset(centerX, dotY)
                    )
                }
            }
            if (isCurrent) {
                Icon(
                    imageVector        = Icons.Default.DirectionsBus,
                    contentDescription = "Bus is here",
                    tint               = Color.White,
                    modifier           = Modifier
                        .size(14.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = 13.dp)
                )
            }
        }

        Spacer(Modifier.width(8.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 8.dp, bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text       = stop.name,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    fontSize   = if (isCurrent) 15.sp else 14.sp,
                    color      = if (isCurrent) TextPrimary else
                        if (stop.state == StopState.PASSED) TextPrimary
                        else TextSecondary,
                    modifier   = Modifier.weight(1f)
                )
                Text(
                    text      = stop.etaText,
                    fontSize  = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color     = when (stop.state) {
                        StopState.PASSED  -> TextPrimary
                        StopState.CURRENT -> BrandOrange
                        else              -> TextSecondary
                    }
                )
            }

            if (stop.reporterText.isNotEmpty()) {
                Text(
                    text     = stop.reporterText,
                    fontSize = 11.sp,
                    color    = TextHint,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, RoundedCornerShape(50))
        )
        Spacer(Modifier.width(6.dp))
        Text(text = label, fontSize = 12.sp, color = TextSecondary)
    }
}