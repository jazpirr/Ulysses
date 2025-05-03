package cit.edu.ulysses.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cit.edu.ulysses.R
import cit.edu.ulysses.app.UserApplication
import cit.edu.ulysses.helpers.UserHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)


        var tf_dob = findViewById<EditText>(R.id.dob)
        var tf_email = findViewById<EditText>(R.id.email)
        var tf_phone = findViewById<EditText>(R.id.number)
        var btn_home = findViewById<ImageButton>(R.id.btnback)
        var btn_delete = findViewById<Button>(R.id.button_delete)
        val btn_save = findViewById<Button>(R.id.btn_save)

        val app = application as UserApplication
        val currentUser = auth.currentUser

        currentUser?.uid?.let { uid ->
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    val username = document.getString("username") ?: "- not set -"
                    val email = document.getString("email") ?: ""
                    val phone = document.getString("phone") ?: ""
                    val dob = document.getString("dob") ?: ""

                    tf_email.setText(email)
                    tf_dob.setText(dob)
                    tf_phone.setText(phone)

                    app.username = username
                    app.email = email
                    app.phone = phone
                    app.dob = dob
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load profile data.", Toast.LENGTH_SHORT).show()
                }
        }

        btn_home.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("openFragment", "settings")
            startActivity(intent)
        }

        btn_save.setOnClickListener {
            val newEmail = tf_email.text.toString().trim()
            val newPhone = tf_phone.text.toString().trim()
            val newDob = tf_dob.text.toString().trim()

            if (newEmail.isEmpty() || newPhone.isEmpty() || newDob.isEmpty()) {
                Toast.makeText(this, "All fields must be filled.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val uid = currentUser?.uid
            if (uid != null) {
                val updates = mapOf(
                    "email" to newEmail,
                    "phone" to newPhone,
                    "dob" to newDob
                )

                db.collection("users").document(uid).update(updates)
                    .addOnSuccessListener {
                        app.email = newEmail
                        app.phone = newPhone
                        app.dob = newDob
                        Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        btn_delete.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete Account")
            builder.setMessage("Are you sure you want to delete this account?")
            builder.setPositiveButton("Yes") { _, _ ->
                val userDb = UserHelper(this)
                userDb.deleteUser(app.username)

                app.email = ""
                app.phone = ""
                app.dob = ""
                app.username = ""
                app.password = ""

                currentUser?.uid?.let { uid ->
                    db.collection("users").document(uid).delete()
                }

                Toast.makeText(this, "You have successfully deleted your account", Toast.LENGTH_LONG).show()

                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            builder.setNegativeButton("Cancel", null)
            builder.show()
        }
    }
}