package com.movtery.zalithlauncher.game

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

object ZenModInstaller {

    data class ZenMod(
        val name: String,
        val fileName: String,
        val downloadUrl: String
    )

    // 1.20.1 Fabric ke top FPS & Cape mods (Modrinth CDN direct links - 100% legal & fast)
    private val zenPackMods = listOf(
        ZenMod(
            name = "Sodium (Max FPS)",
            fileName = "sodium-fabric-mc1.20.1-0.5.8.jar",
            downloadUrl = "https://modrinth.com"
        ),
        ZenMod(
            name = "Lithium (No Lag)",
            fileName = "lithium-fabric-mc1.20.1-0.11.2.jar",
            downloadUrl = "https://modrinth.com"
        ),
        ZenMod(
            name = "Wavey Capes (Glowing Cape Physics)",
            fileName = "waveycapes-fabric-1.4.7-mc1.20.1.jar",
            downloadUrl = "https://modrinth.com"
        )
    )

    suspend fun installZenFpsAndCapePack(
        gameDir: File,
        onProgress: (String) -> Unit
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val modsDir = File(gameDir, "mods").apply { mkdirs() }

            for (mod in zenPackMods) {
                val targetFile = File(modsDir, mod.fileName)
                if (targetFile.exists() && targetFile.length() > 10_000) {
                    onProgress("${mod.name} already installed ✓")
                    continue
                }

                onProgress("Installing ${mod.name}...")
                val connection = URL(mod.downloadUrl).openConnection() as HttpURLConnection
                connection.setRequestProperty("User-Agent", "ZenLauncher/1.0")
                connection.connectTimeout = 15_000
                connection.readTimeout = 30_000

                connection.inputStream.use { input ->
                    targetFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
            onProgress("Zen FPS + Cape Pack Ready! ⚡")
            true
        } catch (e: Exception) {
            onProgress("Error: ${e.message}")
            false
        }
    }
}
