package cit.edu.ulysses.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.core.os.postDelayed
import cit.edu.ulysses.activities.OverlayActivity
//import java.util.logging.Handler

class AppMonitorAccessibilityService : AccessibilityService() {
    companion object {
        private const val TAG = "AppMonitorAccessibility"
        private var instance: AppMonitorAccessibilityService? = null
        fun getInstance(): AppMonitorAccessibilityService? {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun onServiceConnected() {
        Log.d(TAG, "AccessibilityService connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: ""
            val className = event.className?.toString() ?: ""
            Log.d(TAG, "packageName: $packageName preprocessed")



            // Skip system UI and launchers
            if (isHomeScreen(packageName, className) || isSystemUI(packageName)) {
                return
            }

            // Skip our own app's activities except for the blocked app detection
            if (packageName == this.packageName ) {
                return
            }

            Log.d(TAG, "packageName: $packageName, className: $className")
            // Check if this is a blocked app
            if (isAppMonitored(packageName)) {
                Log.d(TAG, "Blocking app")
                launchOverlayActivity(packageName)
            }
        }
        handler.post(taskChecker)
    }

    private val handler = Handler(Looper.getMainLooper())
    private val taskChecker = object : Runnable {
        override fun run() {
            getTopApp(this@AppMonitorAccessibilityService)
            handler.postDelayed(this, 1000)
        }
    }

    fun getTopApp(context: Context) {
        val usageStatsManager = context.getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        val time = System.currentTimeMillis()
        val appList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time
        )

        val recentApp = appList?.maxByOrNull { it.lastTimeUsed }
        Log.d(TAG, "Recent app checking: $recentApp")
        if(recentApp != null){
            if(isAppMonitored(recentApp.packageName)){
                launchOverlayActivity(recentApp.packageName)
            }
        }
    }


    private fun isHomeScreen(packageName: String, className: String): Boolean {
        return packageName.contains("launcher") ||
                className.contains("Launcher") ||
                packageName == "com.android.launcher" ||
                packageName == "com.android.launcher3"
    }

    private fun isSystemUI(packageName: String): Boolean {
        return packageName == "com.android.systemui"
    }

    private fun isAppMonitored(packageName: String): Boolean {
        val blockedApps = getSharedPreferences("appPref", MODE_PRIVATE)
            .getStringSet("selected_apps", emptySet())
        return blockedApps?.contains(packageName) == true
    }

    private fun launchOverlayActivity(packageName: String) {
        Log.d(TAG, "Launching overlay activity for blocked app: $packageName")

        val intent = Intent(this, OverlayActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            putExtra("packageName", packageName)
            putExtra("appName", getAppName(packageName))
        }
        startActivity(intent)
    }

    private fun getAppName(packageName: String): String {
        try {
            val packageManager = applicationContext.packageManager
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            return packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting app name: ${e.message}")
            return packageName
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "AccessibilityService interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
}