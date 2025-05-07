package cit.edu.ulysses.activities


import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import cit.edu.ulysses.R
import cit.edu.ulysses.services.AppMonitorAccessibilityService
import cit.edu.ulysses.utils.GifUtils

class OverlayActivity : AppCompatActivity() {
    private lateinit var countdownTextView: TextView
    private var countdownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.overlay_reminder)

        val appName = intent.getStringExtra("appName") ?: "Unknown App"
        val lockedUntil = getSharedPreferences("lockPrefs", MODE_PRIVATE).getLong("lock_duration", 0L)
        val countdownMillis = lockedUntil - System.currentTimeMillis()

        val messageTextView = findViewById<TextView>(R.id.messageTextView)
        val homeButton = findViewById<Button>(R.id.homeButton)
        countdownTextView = findViewById(R.id.countdownTextView)
        val continueButton = findViewById<TextView>(R.id.continueButton)

        messageTextView.text = "You've chosen to limit your time on $appName"

        homeButton.setOnClickListener {
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

        if (countdownMillis > 0) {
            startCountdown(countdownMillis)
        } else {
            countdownTextView.text = "Lock expired"
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Disable back press functionality
            }
        })
    }

    private fun startCountdown(durationMillis: Long) {
        countdownTimer?.cancel()

        countdownTimer = object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hours = millisUntilFinished / (1000 * 60 * 60)
                val minutes = (millisUntilFinished / (1000 * 60)) % 60
                val seconds = (millisUntilFinished / 1000) % 60
                countdownTextView.text = String.format(
                    "Lock ends in: %02d:%02d:%02d",
                    hours, minutes, seconds
                )
            }

            override fun onFinish() {
                countdownTextView.text = "Lock expired"
                finish()
            }
        }.start()
    }
}
