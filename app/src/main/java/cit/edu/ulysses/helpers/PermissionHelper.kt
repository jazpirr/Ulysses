package cit.edu.ulysses.helpers

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION
import androidx.core.net.toUri
import cit.edu.ulysses.R

object PermissionHelper {
    const val OVERLAY_PERMISSION_REQUEST_CODE = 1001
    const val ACCESSIBILITY_PERMISSION_REQUEST_CODE = 1002

    fun checkAndRequestPermissions(activity: Activity) {
        checkOverlayPermission(activity)
        checkAccessibilityPermission(activity)
        checkNotificationListenerPermission(activity)
    }


    private fun checkOverlayPermission(activity: Activity) {
        if (!Settings.canDrawOverlays(activity)) {
            val dialog = AlertDialog.Builder(activity)
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
                .create()

            dialog.setOnShowListener {
                dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_background)

                dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
                    setBackgroundColor(activity.getColor(R.color.black))
                    setTextColor(activity.getColor(R.color.white))
                }
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
                    setTextColor(activity.getColor(R.color.black))
                }
            }
            dialog.show()
        }
    }

    private fun checkAccessibilityPermission(activity: Activity) {
        if (!isAccessibilityServiceEnabled(activity)) {
            val dialog = AlertDialog.Builder(activity)
                .setTitle("Permission Required")
                .setMessage("Please enable Accessibility Service for this app to function properly.")
                .setPositiveButton("Open Settings") { _, _ ->
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    activity.startActivity(intent)
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .create()

            dialog.setOnShowListener {
                dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_background)

                dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
                    setBackgroundColor(activity.getColor(R.color.black))
                    setTextColor(activity.getColor(R.color.white))
                }
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
                    setTextColor(activity.getColor(R.color.black))
                }
            }

            dialog.show()
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

    fun checkNotificationListenerPermission(activity: Activity) {
        if (!isNotificationListenerEnabled(activity)) {
            val dialog = AlertDialog.Builder(activity)
                .setTitle("Permission Required")
                .setMessage("Please enable 'Notification access' for this app to monitor notifications.")
                .setPositiveButton("Open Settings") { _, _ ->
                    activity.startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .create()
            dialog.setOnShowListener {
                dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_background)

                dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
                    setBackgroundColor(activity.getColor(R.color.black))
                    setTextColor(activity.getColor(R.color.white))
                }
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
                    setTextColor(activity.getColor(R.color.black))
                }
            }
            dialog.show()
        }
    }

    fun isNotificationListenerEnabled(context: Context): Boolean {
        val packageName = context.packageName
        val flat = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        )
        return flat?.contains(packageName) == true
    }

}
