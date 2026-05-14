package com.transit.gramayatri.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.transit.gramayatri.data.repository.userRepo
import com.transit.gramayatri.ui.theme.*

@Composable
fun accountCreationScreen(
    userRepository: userRepo,
    onProfileSaved: () -> Unit
) {
    var name       by remember { mutableStateOf("") }
    var phone      by remember { mutableStateOf("") }
    var nameError  by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text       = "🚌",
                fontSize   = 56.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text       = "Grama-Yatri",
                style      = MaterialTheme.typography.headlineMedium,
                color      = BrandOrange,
                fontWeight = FontWeight.Bold
            )
            Text(
                text  = "Community bus tracker",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(Modifier.height(40.dp))

            Text(
                text       = "Create your account",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color      = TextPrimary,
                modifier   = Modifier.align(Alignment.Start)
            )
            Text(
                text     = "Your name will show on pings you submit.",
                style    = MaterialTheme.typography.bodyMedium,
                color    = TextSecondary,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(Modifier.height(20.dp))
            OutlinedTextField(
                value         = name,
                onValueChange = { name = it; nameError = "" },
                label         = { Text("Your Name") },
                placeholder   = { Text("e.g. Ravi") },
                isError       = nameError.isNotEmpty(),
                supportingText = if (nameError.isNotEmpty()) {
                    { Text(nameError, color = CancelRed) }
                } else null,
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value         = phone,
                onValueChange = { phone = it; phoneError = "" },
                label         = { Text("Phone Number") },
                placeholder   = { Text("e.g. 9844817327") },
                isError       = phoneError.isNotEmpty(),
                supportingText = if (phoneError.isNotEmpty()) {
                    { Text(phoneError, color = CancelRed) }
                } else null,
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(Modifier.height(28.dp))
            Button(
                onClick = {
                    // Validation
                    var valid = true
                    if (name.isBlank()) {
                        nameError = "Please enter your name"
                        valid = false
                    }
                    if (phone.isBlank() || phone.length < 10) {
                        phoneError = "Please enter a valid 10-digit number"
                        valid = false
                    }
                    if (valid) {
                        userRepository.saveProfile(name, phone)
                        onProfileSaved()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
            ) {
                Text(
                    text     = "Confirm",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}