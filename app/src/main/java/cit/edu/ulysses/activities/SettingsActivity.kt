package cit.edu.ulysses.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cit.edu.ulysses.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val tv_name = findViewById<TextView>(R.id.tv_name)
        val tv_email = findViewById<TextView>(R.id.tv_email)

        val logoutButton = findViewById<Button>(R.id.btn_logout)
        logoutButton.setOnClickListener {
            showLogoutConfirmation()
        }
        val profile = findViewById<Button>(R.id.btn_edit)
        profile.setOnClickListener {
            startActivity(Intent(this, EditProfile::class.java))
        }
        val password = findViewById<LinearLayout>(R.id.btn_changePass)
        val report = findViewById<LinearLayout>(R.id.btn_report)
        val terms = findViewById<LinearLayout>(R.id.btn_terms)
        val about = findViewById<LinearLayout>(R.id.btn_about)
        about.setOnClickListener{
            startActivity(Intent(this, DeveloperActivity::class.java))
        }
        terms.setOnClickListener{
            startActivity(Intent(this, TermsActivity::class.java))
        }
        report.setOnClickListener{
            reportProblem()
        }
        password.setOnClickListener{
            startActivity(Intent(this, PasswordActivity::class.java))
        }

        var btn_back = findViewById<ImageButton>(R.id.btn_back)
        btn_back.setOnClickListener {
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
            } else {
                finish()
            }
        }



        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val username = document.getString("username") ?: "Unknown"
                        val email = document.getString("email") ?: "Unknown"

                        tv_name.text = username
                        tv_email.text = email
                    } else {
                        Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load user data: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
        }
//    }

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

    private fun reportProblem(){
        val email = "Ulysses@gmail.com"
        val subject = "Reporting for app ${getString(R.string.app_name)}"

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, "")
        }

        startActivity(Intent.createChooser(intent, "Choose email client:"))
    }
}