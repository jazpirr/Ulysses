package cit.edu.ulysses.helpers

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import android.app.usage.UsageStatsManager

class AppUsage(private val context: Context) {

    // Function to check and request permission
    fun checkUsagePermission() {
        if (isUsagePermissionGranted()) {
            // Proceed with monitoring app usage
            Toast.makeText(context, "Usage Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            // Request permission by redirecting to the usage access settings
            context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
    }

    // Check if the usage access permission is granted
    private fun isUsagePermissionGranted(): Boolean {
        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val startTime = endTime - (1000 * 60 * 10) // 10 minutes ago
        val stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
        return stats != null && stats.isNotEmpty()
    }
}
