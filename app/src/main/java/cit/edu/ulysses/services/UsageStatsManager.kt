package cit.edu.ulysses.services

import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import cit.edu.ulysses.activities.OverlayActivity

class UsageStatsService : Service() {

    private lateinit var usm: UsageStatsManager
    private val sharedPref by lazy { getSharedPreferences("MyPrefs", Context.MODE_PRIVATE) }
    private var isOverlayShown = false
    private var currentBlockedPackage: String? = null

    override fun onCreate() {
        super.onCreate()
        usm = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Thread {
            monitorAppUsage()
        }.start()
        return START_STICKY
    }
    private fun monitorAppUsage() {
        var isOverlayShown = false
        var lastForegroundApp = ""

        while (true) {
            val endTime = System.currentTimeMillis()
            val startTime = endTime - 1000

            val stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
            val recentStats = stats.maxByOrNull { it.lastTimeUsed }
            val currentApp = recentStats?.packageName ?: ""

            val blockedApps = getBlockedApps()

            if (blockedApps.contains(currentApp)) {
                if (!isOverlayShown && lastForegroundApp != currentApp) {
                    showOverlay(currentApp)
                    isOverlayShown = true
                    lastForegroundApp = currentApp
                }
            } else {
                if (isOverlayShown) {
                        closeOverlay()
                    isOverlayShown = false
                    lastForegroundApp = ""
                }
            }

            Thread.sleep(1000)
        }
    }



    private fun checkBlockedApp(packageName: String) {
        val blockedApps = getBlockedApps()

        if (blockedApps.contains(packageName)) {
            showOverlay(packageName)
        }
    }

    private fun getBlockedApps(): List<String> {
        val selectedApps = sharedPref.getStringSet("selected_apps", emptySet()) ?: emptySet()
        return selectedApps.toList()
    }


    private fun showOverlay(packageName: String) {
        val overlayIntent = Intent(this, OverlayActivity::class.java)
        overlayIntent.putExtra("packageName", packageName)
        overlayIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(overlayIntent)
    }

    private fun closeOverlay() {
        println("closedddd")
        val closeIntent = Intent(this, OverlayActivity::class.java)
        closeIntent.action = "CLOSE_OVERLAY"
        closeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(closeIntent)
    }


    override fun onBind(intent: Intent?): IBinder? = null
}
