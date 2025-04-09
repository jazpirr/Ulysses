package cit.edu.ulysses.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cit.edu.ulysses.R
class OverlayActivity : AppCompatActivity() {

    private lateinit var windowManager: WindowManager
    private lateinit var layout: View

    private val closeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "CLOSE_OVERLAY") {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layout = LayoutInflater.from(this).inflate(R.layout.activity_overlay, null)

        val overlayText = layout.findViewById<TextView>(R.id.overlay_text)
        overlayText.text = "App Blocked"

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.addView(layout, params)
        registerReceiver(closeReceiver, IntentFilter("CLOSE_OVERLAY"), RECEIVER_NOT_EXPORTED)

    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
        windowManager.removeView(layout)
        unregisterReceiver(closeReceiver)
    }
}
