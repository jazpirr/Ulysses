package cit.edu.ulysses.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import cit.edu.ulysses.fragment.ResetPasswordBottomSheet
import cit.edu.ulysses.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PasswordActivity : AppCompatActivity() {

    private lateinit var tvVerified: TextView
    private lateinit var btnVerifyLayout: LinearLayout
    private lateinit var btnchange: ImageButton

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_pass)

        tvVerified = findViewById(R.id.tv_verified)
        btnVerifyLayout = findViewById(R.id.btn_report)
        btnchange = findViewById(R.id.change)
        updateVerificationStatus()

        btnchange.isEnabled = false

        var btn_back = findViewById<ImageButton>(R.id.btn_back)
        btn_back.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        btnchange.setOnClickListener{
            val user = auth.currentUser
            if (user != null && !user.isEmailVerified) {
                showEmailNotVerifiedDialog()
            } else {
                val resetPasswordBottomSheet = ResetPasswordBottomSheet()
                resetPasswordBottomSheet.show(supportFragmentManager, resetPasswordBottomSheet.tag)
            }
        }

        btnVerifyLayout.setOnClickListener {
            val user = auth.currentUser
            if (user != null) {
                if (!user.isEmailVerified) {
                    user.sendEmailVerification()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Verification email sent to ${user.email}", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    user.reload().addOnSuccessListener {
                        updateVerificationStatus()
                    }
                }
            }
        }
    }

    private fun updateVerificationStatus() {
        val user = auth.currentUser
        if (user != null) {
            user.reload().addOnSuccessListener {
                if (user.isEmailVerified) {
                    tvVerified.text = "Verified"
                    tvVerified.setTextColor(ContextCompat.getColor(this, R.color.black))

                    btnchange.isEnabled = true

                    firestore.collection("users").document(user.uid)
                        .update("isEmailVerified", true)
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to update Firestore.", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    tvVerified.text = "Not Verified"

                    btnchange.isEnabled = false
                }
            }
        }
    }

    private fun showEmailNotVerifiedDialog() {
        val builder = AlertDialog.Builder(this)
            .setTitle("Email Not Verified")
            .setMessage("You must verify your email before you can change your password. Please check your inbox for a verification email.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("Resend Verification") { dialog, _ ->
                resendVerificationEmail()
                dialog.dismiss()
            }

        builder.create().show()
    }

    private fun resendVerificationEmail() {
        val user = auth.currentUser
        if (user != null) {
            user.sendEmailVerification()
                .addOnSuccessListener {
                    Toast.makeText(this, "Verification email sent to ${user.email}", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to resend verification email.", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
