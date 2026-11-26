package com.movtery.zalithlauncher.ui.screens.content.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import com.movtery.zalithlauncher.game.ZenCapeManager
import com.movtery.zalithlauncher.game.ZenModInstaller
import com.movtery.zalithlauncher.game.ZenReplayManager
import com.movtery.zalithlauncher.path.PathManager
import com.movtery.zalithlauncher.ui.components.BackgroundCard
import com.movtery.zalithlauncher.ui.screens.content.home.server.ServerCardContent
import com.movtery.zalithlauncher.ui.screens.content.home.version.VersionCardContent
import kotlinx.coroutines.launch
import java.io.File

class SystemCard(val id: String, val content: @Composable () -> Unit)

val CardIconShape = RoundedCornerShape(percent = 28)

object HomeCards {
    const val VERSION_CARD_TYPE_ID = "version_card"
    const val SERVER_CARD_TYPE_ID = "server_card"

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

    fun systemCards(): List<SystemCard> = buildList {
        add(zenControlCard())
        add(zenGlowingCapeCard())
    }

    private fun zenControlCard() = SystemCard(id = "system_zen_controls") {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        LaunchedEffect(Unit) { ZenReplayManager.init(context) }

        val replayEnabled = ZenReplayManager.isReplayEnabled
        var modStatus by remember { mutableStateOf("1-CLICK FPS + CAPE MODS") }
        var isInstalling by remember { mutableStateOf(false) }

        BackgroundCard(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0A0A0A))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "⚡ ZENLAUNCHER PRO CONTROLS",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 30s Replay ON/OFF Toggle Button
                    Button(
                        onClick = { ZenReplayManager.toggleReplayMode(context) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (replayEnabled) Color.White else Color(0xFF1F1F1F),
                            contentColor = if (replayEnabled) Color.Black else Color.White
                        )
                    ) {
                        Text(
                            text = if (replayEnabled) "30S REPLAY: ON" else "30S REPLAY: OFF",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // 1-Click FPS + Cape Mods Button
                    OutlinedButton(
                        onClick = {
                            if (!isInstalling) {
                                isInstalling = true
                                scope.launch {
                                    val gameDir = File(PathManager.DIR_GAME_HOME)
                                    ZenModInstaller.installZenFpsAndCapePack(gameDir) { status ->
                                        modStatus = status
                                    }
                                    isInstalling = false
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text(text = modStatus, maxLines = 1, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    private fun zenGlowingCapeCard() = SystemCard(id = "system_zen_glowing_cape") {
        val context = LocalContext.current
        val userCode = remember { ZenCapeManager.getUserInviteCode(context) }
        var isUnlocked by remember { mutableStateOf(ZenCapeManager.isGlowingCapeUnlocked(context)) }
        var keyInput by remember { mutableStateOf("") }

        BackgroundCard(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0A0A0A))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isUnlocked) "✦ ZEN GLOWING CAPE: ACTIVE" else "✦ FREE GLOWING CAPE (3 INVITES)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("ZenCode", userCode))
                            Toast.makeText(context, "Copied: $userCode — Paste in Discord!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text(text = "CODE: $userCode", fontWeight = FontWeight.Bold)
                    }
                }

                if (!isUnlocked) {
                    Text(
                        text = "Invite 3 friends to ZenLauncher Discord with your code to unlock the exclusive White & Black Glowing Cape!",
                        color = Color(0xFFB0B0B0),
                        style = MaterialTheme.typography.bodySmall
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = keyInput,
                            onValueChange = { keyInput = it },
                            placeholder = { Text("Enter CAPE-XXXXXX", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.DarkGray
                            )
                        )
                        Button(
                            onClick = {
                                if (ZenCapeManager.verifyAndUnlockCape(context, keyInput)) {
                                    isUnlocked = true
                                    Toast.makeText(context, "GLOWING CAPE UNLOCKED!", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Invalid Key! Get yours on Discord.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            )
                        ) {
                            Text("UNLOCK", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    
