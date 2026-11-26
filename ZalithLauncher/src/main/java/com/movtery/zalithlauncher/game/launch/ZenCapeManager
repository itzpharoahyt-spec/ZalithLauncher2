package com.movtery.zalithlauncher.game

import android.content.Context
import android.provider.Settings
import java.security.MessageDigest

object ZenCapeManager {

    private const val PREFS_NAME = "zen_cape_prefs"
    private const val KEY_UNLOCKED = "glowing_cape_unlocked"
    private const val KEY_SELECTED_CAPE = "selected_cape_id"
    
    // Tera secret salt - isse koi fake unlock key nahi bana payega
    private const val SECRET_SALT = "ZEN_LAUNCHER_2026_BILLIONAIRE"

    // User ka unique 6-character invite code (e.g., ZEN-A9F2)
    fun getUserInviteCode(context: Context): String {
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "DEFAULT_ZEN_ID"
        val hash = sha256(androidId).take(4).uppercase()
        return "ZEN-$hash"
    }

    // Discord pe 3 invites complete hone par milne wali key generate/verify karna
    fun generateValidUnlockKey(inviteCode: String): String {
        val raw = "${inviteCode.trim().uppercase()}_$SECRET_SALT"
        return "CAPE-" + sha256(raw).take(6).uppercase()
    }

    // User jab launcher mein key dalega toh ye check karega
    fun verifyAndUnlockCape(context: Context, enteredKey: String): Boolean {
        val userCode = getUserInviteCode(context)
        val expectedKey = generateValidUnlockKey(userCode)
        
        return if (enteredKey.trim().uppercase() == expectedKey) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .putBoolean(KEY_UNLOCKED, true)
                .putString(KEY_SELECTED_CAPE, "zen_white_black_glow")
                .apply()
            true
        } else {
            false
        }
    }

    fun isGlowingCapeUnlocked(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_UNLOCKED, false)
    }

    // Minecraft launch arguments mein custom cape/skin server inject karne ke liye
    fun getCapeLaunchJvmArgs(context: Context): List<String> {
        if (!isGlowingCapeUnlocked(context)) return emptyList()
        return listOf(
            "-Dzen.cape.enabled=true",
            "-Dzen.cape.style=white_black_glow"
        )
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
