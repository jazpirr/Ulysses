package cit.edu.ulysses.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import cit.edu.ulysses.R

class SettingsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val logoutButton = findViewById<Button>(R.id.logout_button)
        logoutButton.setOnClickListener {
            showLogoutConfirmation()
        }
        val aboutUs = findViewById<Button>(R.id.about_dev)
        aboutUs.setOnClickListener {
            startActivity(Intent(this, DeveloperActivity::class.java))
        }
        val profile = findViewById<Button>(R.id.btn_profile)
        profile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun showLogoutConfirmation() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to log out?")
        builder.setPositiveButton("OK") { _, _ ->
            val intent = Intent(this, LoginActivity::class.java)
            Toast.makeText(this, "You have successfully logged out", Toast.LENGTH_LONG).show();
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
}