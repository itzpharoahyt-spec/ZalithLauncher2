package com.movtery.zalithlauncher.game.launch

import android.content.Context
import java.io.File
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ZenCapeManager {

    private const val PREFS_NAME = "zen_cape_prefs"
    private const val KEY_UNLOCKED = "glowing_cape_unlocked"

    // Tera Discord Invite Link (Apna permanent link yahan daal dena!)
    const val DISCORD_INVITE_URL = "https://discord.gg"

    // Ye code tu apne Discord ke #10-invites-reward channel mein dalega (bas mahine mein ek baar badal sakta hai!)
    private const val VALID_SECRET_CODE = "ZEN10-GLOW2026"

    // GitHub pe teri White & Black Glowing Cape PNG ka direct link
    const val CAPE_TEXTURE_URL = "https://githubusercontent.com"

    fun verifyAndUnlockCape(context: Context, enteredCode: String): Boolean {
        val clean = enteredCode.trim().uppercase()
        return if (clean == VALID_SECRET_CODE) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_UNLOCKED, true)
                .apply()
            true
        } else {
            false
        }
    }

    fun isGlowingCapeUnlocked(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_UNLOCKED, false)
    }

    // Game launch hone se pehle teri White & Black Glowing Cape PNG download karke ready rakhega
    suspend fun prepareCapeTexture(context: Context, gameDir: File): File? = withContext(Dispatchers.IO) {
        if (!isGlowingCapeUnlocked(context)) return@withContext null
        try {
            val capeDir = File(gameDir, "zen_assets").apply { mkdirs() }
            val capeFile = File(capeDir, "zen_glowing_cape.png")
            if (!capeFile.exists() || capeFile.length() < 100) {
                URL(CAPE_TEXTURE_URL).openStream().use { input ->
                    capeFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
            capeFile
        } catch (_: Exception) {
            null
        }
    }
}
