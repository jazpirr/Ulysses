package cit.edu.ulysses.services

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import cit.edu.ulysses.activities.OverlayActivity
import cit.edu.ulysses.data.AppUsageState
import androidx.core.content.edit

class AppMonitorAccessibilityService : AccessibilityService() {
    companion object {
        private const val TAG = "AppMonitorAccessibility"
        private var instance: AppMonitorAccessibilityService? = null
        private val handler = Handler(Looper.getMainLooper())
        private val launchTasks = mutableMapOf<String, Runnable>()
        private val usageStates = mutableMapOf<String, AppUsageState>()
        fun getInstance(): AppMonitorAccessibilityService? = instance
    }

    private var currentForegroundApp: String? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun onServiceConnected() {
        Log.d(TAG, "AccessibilityService connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return
            val className = event.className?.toString() ?: ""

            if (isHomeScreen(packageName, className) || isSystemUI(packageName) || packageName == this.packageName) {
                return
            }

            if (packageName != currentForegroundApp) {
                currentForegroundApp?.let { pauseAppCountdown(it) }
                currentForegroundApp = packageName
            }

            Log.d(TAG, "packageName: $packageName, className: $className")
            if (isAppMonitored(packageName) && isLockedByDate()) {
                Log.d(TAG, "App is monitored")
                handleMonitoredApp(packageName)
            }
        }
    }

    private fun handleMonitoredApp(packageName: String) {
        val durationSeconds = getOverlayDelaySeconds()
        val usageState = usageStates.getOrPut(packageName) { AppUsageState() }

        if (durationSeconds == 0) {
            launchOverlayActivity(packageName)
            return
        }

        val remainingTime = calculateRemainingTime(packageName, durationSeconds)
        
        if (remainingTime <= 0) {
            launchOverlayActivity(packageName)
            usageState.reset()
        } else {
            startCountdown(packageName, remainingTime)
        }
    }

    private fun calculateRemainingTime(packageName: String, durationSeconds: Int): Long {
        val usageState = usageStates.getOrPut(packageName) { AppUsageState() }
        val totalAllowedTime = durationSeconds * 1000L
        
        if (!usageState.isRunning) {
            usageState.startTime = System.currentTimeMillis()
            usageState.isRunning = true
        }

        val currentTime = System.currentTimeMillis()
        val elapsedTime = usageState.elapsedTime + (currentTime - usageState.startTime)
        return totalAllowedTime - elapsedTime
    }

    private fun startCountdown(packageName: String, remainingTime: Long) {
        launchTasks[packageName]?.let {
            handler.removeCallbacks(it)
            launchTasks.remove(packageName)
        }

        val runnable = Runnable {
            if (isLockedByDate() && currentForegroundApp == packageName) {
                launchOverlayActivity(packageName)
                usageStates[packageName]?.reset()
            }
            launchTasks.remove(packageName)
        }

        handler.postDelayed(runnable, remainingTime)
        launchTasks[packageName] = runnable
        Log.d(TAG, "Timer scheduled for $packageName with $remainingTime ms remaining")
    }

    private fun pauseAppCountdown(packageName: String) {
        usageStates[packageName]?.let { state ->
            if (state.isRunning) {
                state.elapsedTime += System.currentTimeMillis() - state.startTime
                state.isRunning = false
                Log.d(TAG, "Paused countdown for $packageName at ${state.elapsedTime}ms")
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

    private fun getOverlayDelaySeconds(): Int {
        val prefs = getSharedPreferences("lockPrefs", MODE_PRIVATE)
        return prefs.getInt("timer_duration", 0)
    }

    private fun isLockedByDate(): Boolean {
        val prefs = getSharedPreferences("lockPrefs", MODE_PRIVATE)
        val lockUntil = prefs.getLong("lock_duration", 0L)
        val currentTime = System.currentTimeMillis()
        return currentTime < lockUntil
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
        return try {
            val packageManager = applicationContext.packageManager
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting app name: ${e.message}")
            packageName
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
