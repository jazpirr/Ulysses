package cit.edu.ulysses.activities

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cit.edu.ulysses.R
import cit.edu.ulysses.utils.clearOnFocus
import cit.edu.ulysses.utils.toText
import cit.edu.ulysses.utils.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Locale
import android.icu.util.Calendar
import android.util.Log

class EditProfile : AppCompatActivity() {
    private val calendar = Calendar.getInstance()
    private lateinit var formattedDate: String
    private lateinit var dob: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile)

        val saveButton = findViewById<Button>(R.id.button_save)
        val username = findViewById<EditText>(R.id.username_edit)
        val email = findViewById<EditText>(R.id.email_edit)
        val phone = findViewById<EditText>(R.id.number_edit)
        val btnDelete = findViewById<Button>(R.id.btn_delete)

        val firestore = Firebase.firestore
        val currentUser = FirebaseAuth.getInstance().currentUser

        dob = findViewById<EditText>(R.id.bday_edit)
        dob.showSoftInputOnFocus = false
        dob.isFocusable = false

        dob.setOnClickListener {
            showDatePicker()
        }

        currentUser?.let { user ->
            val uid = user.uid
            firestore.collection("users").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val currentUsername = document.getString("username")
                        val currentEmail = document.getString("email")
                        val currentPhone = document.getString("phone")
                        val currentDob = document.getString("dob")

                        username.setText(currentUsername)
                        email.setText(currentEmail)
                        phone.setText(currentPhone)
                        dob.setText(currentDob)
                    }
                }
                .addOnFailureListener { e ->
                    toast("Failed to load user data: ${e.message}")
                }
        }

        saveButton.setOnClickListener {
            val newUsername = username.toText()
            val newPhone = phone.toText()
            val newDob = dob.toText()


            val updates = mapOf(
                "username" to newUsername,
                "phone" to newPhone,
                "dob" to newDob
            )

            currentUser?.let { user ->
                firestore.collection("users").document(user.uid)
                    .update(updates)
                    .addOnSuccessListener {
                        toast("Profile updated!")
                        startActivity(Intent(this, SettingsActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener { e ->
                        toast("Firestore update failed: ${e.message}")
                    }
            }

            startActivity(Intent(this, SettingsActivity::class.java))
        }

        val buttonCancel = findViewById<Button>(R.id.button_cancel)
        buttonCancel.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            finish()
        }

        btnDelete.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete Account")
            builder.setMessage("Are you sure you want to delete your account? This action cannot be undone.")
            builder.setPositiveButton("Yes") { _, _ ->
                currentUser?.let { user ->
                    val uid = user.uid
                    firestore.collection("users").document(uid).delete()
                        .addOnSuccessListener {
                            user.delete()
                                .addOnSuccessListener {
                                    FirebaseAuth.getInstance().signOut()
                                    val intent = Intent(this, LoginActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                    toast("Account deleted successfully!")
                                }
                                .addOnFailureListener { e ->
                                    toast("Failed to delete account from Firebase Authentication: ${e.message}")
                                }
                        }
                        .addOnFailureListener { e ->
                            Log.e("ERROR KA", "Failed to delete Firestore document: ${e.message}")
                            toast("Failed to delete Firestore document: ${e.message}")
                        }
                }
            }
            builder.setNegativeButton("Cancel", null)
            builder.show()
        }
    }

    private fun showDatePicker() {
        val dateDialog = DatePickerDialog(
            this,
            { _, year: Int, month: Int, day: Int ->
                calendar.set(year, month, day)
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, day)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                formattedDate = dateFormat.format(selectedDate.time)
                dob.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        dateDialog.window?.setBackgroundDrawableResource(R.color.gray)

        dateDialog.show()
    }
}


