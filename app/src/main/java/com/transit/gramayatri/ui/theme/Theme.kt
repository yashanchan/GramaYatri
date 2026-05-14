package com.transit.gramayatri.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary       = BrandOrange,
    onPrimary     = CardWhite,
    secondary     = BrandOrangeLight,
    onSecondary   = CardWhite,
    background    = SurfaceGray,
    surface       = CardWhite,
    onBackground  = TextPrimary,
    onSurface     = TextPrimary,
    error         = CancelRed
)

@Composable
fun GramaYatriTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography  = Typography,
        content     = content
    )
}