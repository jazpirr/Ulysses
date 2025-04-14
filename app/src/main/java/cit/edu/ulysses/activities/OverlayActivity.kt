//package cit.edu.ulysses.activities
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import android.graphics.PixelFormat
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.WindowManager
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import cit.edu.ulysses.R
//
//class OverlayActivity : AppCompatActivity() {
//
//    private lateinit var windowManager: WindowManager
//    private lateinit var layout: View
//
//    private val closeReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            if (intent?.action == "CLOSE_OVERLAY") {
//                removeOverlayAndFinish()
//            }
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Don't show the normal activity UI
//        setContentView(R.layout.activity_empty)
//
//        // Create the overlay view
//        layout = LayoutInflater.from(this).inflate(R.layout.activity_overlay, null)
//
//        val overlayText = layout.findViewById<TextView>(R.id.overlay_text)
//        overlayText.text = "App Blocked"
//
//        // Add a close button if needed
//        val closeButton = layout.findViewById<Button>(R.id.close_button)
//        closeButton?.setOnClickListener {
//            removeOverlayAndFinish()
//        }
//
//        val params = WindowManager.LayoutParams(
//            WindowManager.LayoutParams.MATCH_PARENT,
//            WindowManager.LayoutParams.MATCH_PARENT,
//            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
//                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
//            PixelFormat.TRANSLUCENT
//        )
//
//        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
//        windowManager.addView(layout, params)
//
//        // Register the receiver for closing the overlay
//        registerReceiver(closeReceiver, IntentFilter("CLOSE_OVERLAY"), RECEIVER_NOT_EXPORTED)
//    }
//
//    private fun removeOverlayAndFinish() {
//        try {
//            if (::windowManager.isInitialized && ::layout.isInitialized) {
//                windowManager.removeView(layout)
//            }
//        } catch (e: Exception) {
//            // Handle exception if view is already removed
//        }
//        finish()
//    }
//
//    override fun onDestroy() {
//        try {
//            if (::windowManager.isInitialized && ::layout.isInitialized) {
//                windowManager.removeView(layout)
//            }
//            unregisterReceiver(closeReceiver)
//        } catch (e: Exception) {
//            // Handle any exceptions during cleanup
//        }
//        super.onDestroy()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        // Remove overlay when app goes to background
//        removeOverlayAndFinish()
//    }
//
//    companion object {
//        // Start this activity when you detect the target app opening
//        fun start(context: Context) {
//            val intent = Intent(context, OverlayActivity::class.java).apply {
//                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            }
//            context.startActivity(intent)
//        }
//    }
//}