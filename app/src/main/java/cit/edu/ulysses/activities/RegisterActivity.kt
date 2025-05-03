package cit.edu.ulysses.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.compose.ui.geometry.isEmpty
import androidx.compose.ui.semantics.text
import androidx.core.content.ContentProviderCompat.requireContext
import cit.edu.ulysses.R
import cit.edu.ulysses.app.UserApplication
import cit.edu.ulysses.utils.isNotValid
import cit.edu.ulysses.utils.toText
import cit.edu.ulysses.utils.toast
import androidx.core.content.edit
import cit.edu.ulysses.helpers.UserHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : Activity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firebaseAuth = FirebaseAuth.getInstance()

        val etusername = findViewById<EditText>(R.id.et_username)
        val etpassword = findViewById<EditText>(R.id.et_pass)
        val etpassword2 = findViewById<EditText>(R.id.et_pass2)
        val etemail = findViewById<EditText>(R.id.et_email)
        val btn_create = findViewById<Button>(R.id.btn_create)
        val userDb = UserHelper(this)

        val loginLink = findViewById<TextView>(R.id.haveAccount)
        loginLink.setOnClickListener { v ->
            val intent =
                Intent(
                    this,
                    LoginActivity::class.java
                )
            startActivity(intent)
        }

        btn_create.setOnClickListener {
            val username = etusername.text.toString()
            val password = etpassword.text.toString()
            val password2 = etpassword2.text.toString()
            val email = etemail.text.toString()

            if (etusername.isNotValid() || etpassword.isNotValid() || etpassword2.isNotValid() || etemail.isNotValid()) {
                toast("All fields must be filled.")
                return@setOnClickListener
            }

            if (password != password2) {
                toast("Passwords do not match.")
                return@setOnClickListener
            }

            checkUsernameAvailability(username) { isAvailable ->
                if (isAvailable) {
                    createUser(email, password, username)
                } else {
                    toast("Username is already taken.")
                }
            }
        }
    }

    private fun checkUsernameAvailability(username: String, callback: (Boolean) -> Unit) {
        db.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                callback(documents.isEmpty)
            }
            .addOnFailureListener { exception ->
                toast("Error checking username availability: ${exception.message}")
                callback(false)
            }
    }

    private fun createUser(email: String, password: String, username: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser?.uid
                    if (user != null) {
                        val userMap = hashMapOf(
                            "username" to username,
                            "email" to email,
                            "phone" to "- not set -",
                            "dob" to "- not set -",
                            "isVerified" to false
                        )

                        db.collection("users").document(user).set(userMap)
                            .addOnSuccessListener {
                                toast("Successfully registered!")
                                val intent = Intent(this, LoginActivity::class.java)
                                intent.putExtra("EMAIL", email)
                                intent.putExtra("PASSWORD", password)
                                startActivity(intent)
                            }
                            .addOnFailureListener { exception ->
                                toast("Failed to add user data: ${exception.message}")
                            }
                    }
                } else {
                    toast(getFirebaseAuthErrorMessage(task.exception))
                }
            }
    }

    private fun fetchUserData(userId: String) {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val username = document.getString("username") ?: ""
                    val email = document.getString("email") ?: ""
                    val phone = document.getString("phone") ?: ""
                    val dob = document.getString("dob") ?: ""

                    findViewById<EditText>(R.id.et_username).setText(username)
                    findViewById<EditText>(R.id.et_email).setText(email)
                    findViewById<EditText>(R.id.et_pass).setText("")
                    findViewById<EditText>(R.id.et_pass2).setText("")
                }
            }
            .addOnFailureListener { exception ->
                toast("Failed to fetch user data: ${exception.message}")
            }
    }

    private fun clearFields() {
        findViewById<EditText>(R.id.et_username).text.clear()
        findViewById<EditText>(R.id.et_pass).text.clear()
        findViewById<EditText>(R.id.et_pass2).text.clear()
        findViewById<EditText>(R.id.et_email).text.clear()
    }

    fun getFirebaseAuthErrorMessage(exception: Exception?): String {
        val message = exception?.message ?: return "Unknown error occurred."

        return when {
            message.contains("email address is already in use", ignoreCase = true) ->
                "This email is already registered. Try logging in instead."
            message.contains("badly formatted", ignoreCase = true) ->
                "Invalid email format."
            message.contains("Password should be at least", ignoreCase = true) ->
                "Password must be at least 6 characters."
            message.contains("network error", ignoreCase = true) ->
                "Network error. Check your internet connection."
            message.contains("WEAK_PASSWORD", ignoreCase = true) ->
                "Password is too weak."
            else -> "Registration failed: ${exception.localizedMessage}"
        }
    }
}