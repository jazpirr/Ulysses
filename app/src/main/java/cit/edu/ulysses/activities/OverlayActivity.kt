package cit.edu.ulysses.activities


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import cit.edu.ulysses.R
import cit.edu.ulysses.services.AppMonitorAccessibilityService

class OverlayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.overlay_reminder)

        // Get data from intent
        val appName = intent.getStringExtra("appName") ?: "Unknown App"
        val messageTextView = findViewById<TextView>(R.id.messageTextView)
        val homeButton = findViewById<Button>(R.id.homeButton)
        val continueButton = findViewById<TextView>(R.id.continueButton)

        messageTextView.text = "You've chosen to limit your time on $appName"

        // Set up button actions
        homeButton.setOnClickListener {
            // Go to home screen
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(homeIntent)
            finish()
        }

        val allowBypass = getSharedPreferences("settings", MODE_PRIVATE)
            .getBoolean("allow_bypass", false)

        if (allowBypass) {
            continueButton.visibility = Button.VISIBLE
            continueButton.setOnClickListener {
                // Allow user to continue using the app for this session
                finish() // Just close this activity
            }
        } else {
            continueButton.visibility = Button.GONE
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){ override fun handleOnBackPressed() {}})
    }

}