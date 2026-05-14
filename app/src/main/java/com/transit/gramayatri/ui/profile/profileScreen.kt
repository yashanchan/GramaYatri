package com.transit.gramayatri.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.transit.gramayatri.data.repository.userRepo
import com.transit.gramayatri.ui.theme.*

@Composable
fun profileScreen(
    userRepository: userRepo,
    onEditProfile: () -> Unit,
    onSavedPrefs: () -> Unit,
    onSettings: () -> Unit,
    onLogOut: () -> Unit
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
    var showLogoutDialog by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { vm.reload() }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log out?") },
            text  = { Text("You will need to re-enter your name and phone number.") },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false; onLogOut() }) {
                    Text("Log out", color = CancelRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceGray)
    ) {
        item {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    "Profile",
                    style      = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        item {
            Card(
                modifier  = Modifier.padding(16.dp),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(AvatarOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text      = profile.name.firstOrNull()?.uppercase() ?: "?",
                            color     = Color.White,
                            fontSize  = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text       = profile.name,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 17.sp
                        )
                        Text(
                            text  = profile.phone,
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text     = "Edit Profile >",
                            color    = BrandOrange,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { onEditProfile() }
                        )
                    }
                }
            }
        }
        item {
            Card(
                modifier  = Modifier.padding(horizontal = 16.dp),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column {
                    ProfileOptionRow(
                        icon     = Icons.Default.Star,
                        title    = "Saved Preferences",
                        subtitle = "Manage favourite routes and stops",
                        onClick  = onSavedPrefs
                    )
                    HorizontalDivider(color = DividerColor, modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileOptionRow(
                        icon     = Icons.Default.Settings,
                        title    = "Settings",
                        subtitle = "Notifications, data usage, app theme",
                        onClick  = onSettings
                    )
                    HorizontalDivider(color = DividerColor, modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileOptionRow(
                        icon      = Icons.Default.Logout,
                        title     = "Log out",
                        subtitle  = "Sign out from this device",
                        titleColor = CancelRed,
                        iconBgColor = CancelRedLight,
                        iconTint    = CancelRed,
                        onClick   = { showLogoutDialog = true }
                    )
                }
            }
        }

        item {
            Text(
                text     = "Grama-Yatri · v1.0",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                color    = TextHint,
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun ProfileOptionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    titleColor: Color   = TextPrimary,
    iconBgColor: Color  = SurfaceGray,
    iconTint: Color     = TextSecondary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = title, tint = iconTint,
                modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, color = titleColor, fontSize = 15.sp)
            Text(subtitle, color = TextSecondary, fontSize = 12.sp)
        }
        Icon(Icons.Default.ChevronRight, null, tint = TextHint, modifier = Modifier.size(20.dp))
    }
}