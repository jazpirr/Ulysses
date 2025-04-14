package cit.edu.ulysses.helpers

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import android.app.usage.UsageStatsManager
import androidx.core.content.ContextCompat.checkSelfPermission

class AppUsage(private val context: Context) {

    fun checkUsagePermission() {
        if (isPermissionGranted()) {
            Toast.makeText(context, "Usage Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
    }

    private fun isPermissionGranted(): Boolean {
        val permissionCheck = checkSelfPermission(context, android.Manifest.permission.BIND_ACCESSIBILITY_SERVICE)
        return permissionCheck == android.content.pm.PackageManager.PERMISSION_GRANTED
    }
}