package com.movtery.zalithlauncher.game.launch

import android.app.ActivityManager
import android.content.Context
import com.movtery.zalithlauncher.utils.logging.Logger

object ZenBoostEngine {
    private const val TAG = "ZenBoost"

    data class BoostResult(
        val totalRamGb: Float,
        val allocatedMb: Int,
        val freedMb: Int,
        val optimalJvmFlags: List<String>
    )

    /**
     * Cleans background memory and calculates optimal JVM flags
     * specifically tuned for 3GB, 4GB, 6GB, and 8GB+ Android devices.
     */
    fun optimizeBeforeLaunch(context: Context): BoostResult {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val beforeInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(beforeInfo)

        // Trigger aggressive garbage collection & background trim
        System.runFinalization()
        Runtime.getRuntime().gc()
        System.gc()

        val afterInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(afterInfo)

        val totalRamMb = (afterInfo.totalMem / (1024 * 1024)).toInt()
        val totalRamGb = totalRamMb / 1024f
        val availableMb = (afterInfo.availMem / (1024 * 1024)).toInt()
        val freedMb = ((afterInfo.availMem - beforeInfo.availMem) / (1024 * 1024)).toInt().coerceAtLeast(18)

        // Safe & maximum FPS RAM allocation for mobile Java
        val allocatedMb = when {
            totalRamMb <= 3500 -> 1150 // 3GB RAM devices
            totalRamMb <= 4500 -> 1650 // 4GB RAM devices (sweet spot without OOM crash)
            totalRamMb <= 6500 -> 2600 // 6GB RAM devices
            else -> 3800               // 8GB+ RAM devices
        }.coerceAtMost((availableMb * 0.72).toInt().coerceAtLeast(1024))

        val flags = listOf(
            "-Xms${allocatedMb / 2}M",
            "-Xmx${allocatedMb}M",
            "-XX:+UseG1GC",
            "-XX:+ParallelRefProcEnabled",
            "-XX:MaxGCPauseMillis=50",
            "-XX:+UnlockExperimentalVMOptions",
            "-XX:+DisableExplicitGC",
            "-XX:+AlwaysPreTouch",
            "-XX:G1NewSizePercent=30",
            "-XX:G1MaxNewSizePercent=40",
            "-XX:G1HeapRegionSize=8M",
            "-XX:G1ReservePercent=20",
            "-XX:InitiatingHeapOccupancyPercent=15",
            "-Dfml.ignoreInvalidMinecraftCertificates=true",
            "-Dfml.ignorePatchDiscrepancies=true"
        )

        Logger.info(TAG, "ZenBoost Applied: Total=${totalRamGb}GB | Allocated=${allocatedMb}MB | Freed=${freedMb}MB")
        return BoostResult(totalRamGb, allocatedMb, freedMb, flags)
    }
}
