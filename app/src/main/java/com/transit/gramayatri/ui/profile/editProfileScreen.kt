package com.transit.gramayatri.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.transit.gramayatri.data.repository.userRepo
import com.transit.gramayatri.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun editProfileScreen(
    userRepository: userRepo,
    onBack: () -> Unit
) {
    val vm: ProfileViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(userRepository) as T
            }
        }
    )

    val profile by vm.profile.collectAsState()
    var name    by remember(profile) { mutableStateOf(profile.name) }
    var phone   by remember(profile) { mutableStateOf(profile.phone) }
    var nameError  by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    var valid = true
                    if (name.isBlank()) { nameError = "Name cannot be empty"; valid = false }
                    if (phone.isBlank() || phone.length < 10) { phoneError = "Enter valid number"; valid = false }
                    if (valid) { vm.updateProfile(name, phone); onBack() }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(52.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
            ) {
                Text("SAVE", fontWeight = FontWeight.Bold, fontSize = 15.sp,
                    letterSpacing = androidx.compose.ui.unit.TextUnit(2f,
                        androidx.compose.ui.unit.TextUnitType.Sp))
            }
        },
        containerColor = SurfaceGray
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(28.dp))
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(AvatarOrange),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    profile.name.firstOrNull()?.uppercase() ?: "?",
                    color     = Color.White,
                    fontSize  = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(28.dp))
            Text("Name", fontWeight = FontWeight.SemiBold, color = TextPrimary,
                modifier = Modifier.align(Alignment.Start))
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value         = name,
                onValueChange = { name = it; nameError = "" },
                isError       = nameError.isNotEmpty(),
                supportingText = if (nameError.isNotEmpty()) {
                    { Text(nameError, color = CancelRed) }
                } else null,
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor      = BrandOrange,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor   = Color.White
                )
            )

            Spacer(Modifier.height(16.dp))
            Text("Mobile Number", fontWeight = FontWeight.SemiBold, color = TextPrimary,
                modifier = Modifier.align(Alignment.Start))
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value         = phone,
                onValueChange = { phone = it; phoneError = "" },
                isError       = phoneError.isNotEmpty(),
                supportingText = if (phoneError.isNotEmpty()) {
                    { Text(phoneError, color = CancelRed) }
                } else null,
                singleLine      = true,
                modifier        = Modifier.fillMaxWidth(),
                shape           = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor      = BrandOrange,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor   = Color.White
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPreferencesScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Preferences", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = SurfaceGray
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint     = BrandOrange,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        "No saved routes yet",
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 16.sp,
                        color      = TextPrimary
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Pin your daily routes from the Bus Search tab to see them here.",
                        textAlign = TextAlign.Center,
                        color     = TextSecondary,
                        fontSize  = 13.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = SurfaceGray
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Spacer(Modifier.height(12.dp))
            Card(
                modifier  = Modifier.padding(horizontal = 16.dp),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column {
                    SettingsRow(Icons.Default.Notifications, "Notifications",
                        "Bus alerts, ping confirmations")
                    HorizontalDivider(color = DividerColor,
                        modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsRow(Icons.Default.SignalCellularAlt, "Data usage",
                        "Reduce data on slow networks")
                    HorizontalDivider(color = DividerColor,
                        modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsRow(Icons.Default.Palette, "App theme",
                        "Light · Dark · System")
                    HorizontalDivider(color = DividerColor,
                        modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsRow(Icons.Default.Translate, "Language", "English")
                }
            }

            Spacer(Modifier.height(20.dp))
            Text(
                "Settings are placeholder for now and will be wired up in a future release.",
                modifier  = Modifier.padding(horizontal = 20.dp),
                color     = TextHint,
                fontSize  = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SettingsRow(icon: androidx.compose.ui.graphics.vector.ImageVector,
                title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SurfaceGray),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = title, tint = TextSecondary,
                modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary)
            Text(subtitle, color = TextSecondary, fontSize = 12.sp)
        }
        Icon(Icons.Default.ChevronRight, null, tint = TextHint, modifier = Modifier.size(20.dp))
    }
}