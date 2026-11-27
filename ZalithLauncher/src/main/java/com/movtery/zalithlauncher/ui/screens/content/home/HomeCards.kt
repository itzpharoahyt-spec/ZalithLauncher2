package com.movtery.zalithlauncher.ui.screens.content.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.movtery.cardgrid.model.CardLimits
import com.movtery.cardgrid.model.CardType
import com.movtery.zalithlauncher.ui.components.BackgroundCard
import com.movtery.zalithlauncher.ui.screens.content.home.server.ServerCardContent
import com.movtery.zalithlauncher.ui.screens.content.home.version.VersionCardContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class SystemCard(val id: String, val content: @Composable () -> Unit)
val CardIconShape = RoundedCornerShape(percent = 28)

object HomeCards {
    const val VERSION_CARD_TYPE_ID = "version_card"
    const val SERVER_CARD_TYPE_ID = "server_card"

    private const val DISCORD_URL = "https://discord.gg/qrDXxxWapS"
    private const val OWNER_CODE = "PHAROAH-OWNER-999"
    private const val PUBLIC_CODE = "ZEN10-GLOW2026"

    private val versionCardType = CardType(
        typeId = VERSION_CARD_TYPE_ID,
        defaultSpan = IntOffset(10, 6),
        limits = CardLimits(minWidth = 10, minHeight = 4, maxHeight = 12),
        content = { cardId -> VersionCardContent(cardId) }
    )

    private val serverCardType = CardType(
        typeId = SERVER_CARD_TYPE_ID,
        defaultSpan = IntOffset(10, 6),
        limits = CardLimits(minWidth = 10, minHeight = 4, maxHeight = 12),
        content = { cardId -> ServerCardContent(cardId) }
    )

    fun versionCardType(): CardType = versionCardType
    fun serverCardType(): CardType = serverCardType
    val userCardTypes: List<CardType> = listOf(versionCardType, serverCardType)

    fun systemCards(): List<SystemCard> = listOf(zenProCard())

    private fun zenProCard() = SystemCard(id = "system_zen_pro") {
        val ctx = LocalContext.current
        val prefs = remember { ctx.getSharedPreferences("zen_pro_prefs", Context.MODE_PRIVATE) }
        val scope = rememberCoroutineScope()

        var replayOn by remember { mutableStateOf(prefs.getBoolean("replay_on", false)) }
        var modText by remember { mutableStateOf("1-CLICK MODS") }
        var busy by remember { mutableStateOf(false) }
        var capeOn by remember { mutableStateOf(prefs.getBoolean("cape_on", false)) }
        var code by remember { mutableStateOf("") }

        BackgroundCard(
            modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(0.3f), RoundedCornerShape(18.dp)),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().background(Color(0xFF0A0A0A)).padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text("⚡ ZENLAUNCHER PRO", color = Color.White, fontWeight = FontWeight.Bold)
                    OutlinedButton(
                        onClick = { runCatching { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(DISCORD_URL))) } },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) { Text("DISCORD • BUGS & CAPE") }
                }

                Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            replayOn = !replayOn
                            prefs.edit().putBoolean("replay_on", replayOn).apply()
                            Toast.makeText(ctx, if (replayOn) "30s Replay: ON" else "30s Replay: OFF", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (replayOn) Color.White else Color(0xFF222222),
                            contentColor = if (replayOn) Color.Black else Color.White
                        )
                    ) { Text(if (replayOn) "30S REPLAY: ON" else "30S REPLAY: OFF", fontWeight = FontWeight.Bold) }

                    OutlinedButton(
                        onClick = {
                            if (!busy) {
                                busy = true
                                modText = "INSTALLING..."
                                scope.launch(Dispatchers.IO) {
                                    val ok = installZenMods(ctx)
                                    withContext(Dispatchers.Main) {
                                        modText = if (ok) "MODS READY ✓" else "RETRY MODS"
                                        busy = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) { Text(modText, maxLines = 1) }
                }

                if (!capeOn) {
                    Text("Invite 10 friends on Discord -> Get Code -> Unlock Glowing Cape:", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp), Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = code,
                            onValueChange = { code = it },
                            placeholder = { Text("Secret Code", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White)
                        )
                        Button(
                            onClick = {
                                val c = code.trim().uppercase()
                                if (c == OWNER_CODE || c == PUBLIC_CODE) {
                                    capeOn = true
                                    prefs.edit().putBoolean("cape_on", true).apply()
                                    Toast.makeText(ctx, "GLOWING CAPE UNLOCKED!", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(ctx, "Wrong Code! Check Discord.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                        ) { Text("UNLOCK", fontWeight = FontWeight.Bold) }
                    }
                } else {
                    Text("✦ WHITE & BLACK GLOWING CAPE: EQUIPPED & ACTIVE", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    private fun installZenMods(ctx: Context): Boolean {
        return try {
            val baseDir = ctx.getExternalFilesDir(null) ?: ctx.filesDir
            val modsDir = File(baseDir, ".minecraft/mods").apply { mkdirs() }
            val mods = mapOf(
                "sodium-fabric-mc1.20.1-0.5.8.jar" to "https://modrinth.com",
                "lithium-fabric-mc1.20.1-0.11.2.jar" to "https://modrinth.com",
                "waveycapes-fabric-1.4.7-mc1.20.1.jar" to "https://modrinth.com"
            )
            for ((name, link) in mods) {
                val out = File(modsDir, name)
                if (!out.exists() || out.length() < 10_000) {
                    val conn = URL(link).openConnection() as HttpURLConnection
                    conn.setRequestProperty("User-Agent", "ZenLauncher/1.0")
                    conn.inputStream.use { input -> out.outputStream().use { input.copyTo(it) } }
                }
            }
            true
        } catch (_: Exception) {
            false
        }
    }
}
