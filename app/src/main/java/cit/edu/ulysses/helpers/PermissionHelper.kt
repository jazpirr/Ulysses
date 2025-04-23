package cit.edu.ulysses.helpers

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION
import androidx.core.net.toUri

object PermissionHelper {
    const val OVERLAY_PERMISSION_REQUEST_CODE = 1001
    const val ACCESSIBILITY_PERMISSION_REQUEST_CODE = 1002

    fun checkAndRequestPermissions(activity: Activity) {
        checkOverlayPermission(activity)
        checkAccessibilityPermission(activity)
    }

    private fun checkOverlayPermission(activity: Activity) {
        if (!Settings.canDrawOverlays(activity)) {
            AlertDialog.Builder(activity)
                .setTitle("Permission Required")
                .setMessage("Please enable 'Draw over other apps' for this app to function properly.")
                .setPositiveButton("Open Settings") { _, _ ->
                    val intent = Intent(
                        ACTION_MANAGE_OVERLAY_PERMISSION,
                        "package:${activity.packageName}".toUri()
                    )
                    activity.startActivity(intent)
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    private fun checkAccessibilityPermission(activity: Activity) {
        if (!isAccessibilityServiceEnabled(activity)) {
            AlertDialog.Builder(activity)
                .setTitle("Permission Required")
                .setMessage("Please enable 'Draw over other apps' for this app to function properly.")
                .setPositiveButton("Open Settings") { _, _ ->
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    activity.startActivity(intent)
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        val expectedService = "${context.packageName}/${cit.edu.ulysses.services.AppMonitorAccessibilityService::class.java.name}"
        return enabledServices?.contains(expectedService) == true
    }

    fun isOverlayPermissionGranted(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }
}
