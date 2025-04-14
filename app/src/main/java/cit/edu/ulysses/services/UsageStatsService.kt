package cit.edu.ulysses.services

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.graphics.PixelFormat
import android.view.LayoutInflater
import android.view.SurfaceControlViewHost
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import cit.edu.ulysses.R
import cit.edu.ulysses.models.AppData
class UsageStatsService : AccessibilityService() {
    private var onCurrentScreen: Boolean = false
    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View

    override fun onServiceConnected() {
        super.onServiceConnected()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val blockedApps = getSharedPreferences("appPref", MODE_PRIVATE)
            .getStringSet("selected_apps", emptySet())

        println("Package  ${event?.packageName}")
        println("Action ${event?.eventType}")
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            val packageName = event.packageName?.toString()
            println("Window state changed")

            if (blockedApps?.contains(packageName) == true && !onCurrentScreen) {
                println("Showing overlay")
                showOverlay()
                onCurrentScreen = true
            } else if (onCurrentScreen) {
                println("Removing overlay")
                removeOverlay()
                onCurrentScreen = false
            }
        }
    }

    private fun showOverlay() {
        if (overlayView.parent == null) {
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
            )
            windowManager.addView(overlayView, params)
        }
    }

    private fun removeOverlay() {
        if (overlayView.parent != null) {
            windowManager.removeView(overlayView)
        }
    }

    override fun onInterrupt() {}
}
