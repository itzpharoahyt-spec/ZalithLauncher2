package com.movtery.zalithlauncher.ui.theme

import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.movtery.zalithlauncher.setting.enums.isLauncherInDarkTheme
import com.movtery.zalithlauncher.ui.theme.components.activeMaskView
import com.movtery.zalithlauncher.utils.festival.Festival
import com.movtery.zalithlauncher.utils.festival.LocalFestivals
import com.movtery.zalithlauncher.viewmodel.BackgroundViewModel
import com.movtery.zalithlauncher.viewmodel.LocalBackgroundViewModel
import top.yukonga.miuix.kmp.utils.MiuixOverscrollFactory

private val ZenDarkScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF1F1F1F),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFD0D0D0),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF1A1A1A),
    onSecondaryContainer = Color.White,
    background = Color(0xFF0A0A0A),
    onBackground = Color.White,
    surface = Color(0xFF0A0A0A),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF161616),
    onSurfaceVariant = Color(0xFFD0D0D0),
    outline = Color(0xFF3A3A3A)
)

private val ZenLightScheme = lightColorScheme(
    primary = Color.Black,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEAEAEA),
    onPrimaryContainer = Color.Black,
    secondary = Color(0xFF333333),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF2F2F2),
    onSecondaryContainer = Color.Black,
    background = Color(0xFFF8F8F8),
    onBackground = Color.Black,
    surface = Color(0xFFF8F8F8),
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFEEEEEE),
    onSurfaceVariant = Color(0xFF222222),
    outline = Color(0xFFBDBDBD)
)

@Composable
fun ZalithLauncherTheme(
    darkTheme: Boolean = isLauncherInDarkTheme(),
    festivals: List<Festival> = emptyList(),
    backgroundViewModel: BackgroundViewModel? = null,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ZenDarkScheme else ZenLightScheme

    CompositionLocalProvider(
        LocalOverscrollFactory provides MiuixOverscrollFactory,
        LocalFestivals provides festivals,
        LocalBackgroundViewModel provides backgroundViewModel
    ) {
        MaterialExpressiveTheme(
            colorScheme = colorScheme,
            motionScheme = MotionScheme.expressive()
        ) {
            content()
            activeMaskView()
        }
    }
}
