package cit.edu.ulysses.activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import cit.edu.ulysses.fragment.ResetPasswordBottomSheet

import cit.edu.ulysses.R
import cit.edu.ulysses.app.UserApplication
import cit.edu.ulysses.helpers.UserHelper
import cit.edu.ulysses.utils.isNotValid
import cit.edu.ulysses.utils.toast
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class LoginActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        var etUsername = findViewById<EditText>(R.id.etUsername)
        var etPassword = findViewById<EditText>(R.id.etPassword)
        var btnLogin = findViewById<Button>(R.id.btnLogin)
        var tvRegisterLink = findViewById<TextView>(R.id.tvRegisterLink)

        etUsername.setText((application as UserApplication).username)
        etPassword.setText((application as UserApplication).password)
        val userDb = UserHelper(this)

        firebaseAuth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener {
            val email = etUsername.text.toString()
            val password = etPassword.text.toString()
            if(etUsername.isNotValid() || etPassword.isNotValid()){
                toast("Username and password cannot be empty")
                return@setOnClickListener
            }
//            val user = userDb.getUserByUsername(etUsername.toText())
//            if(user == null || user.password != etPassword.toText()){
//                toast("Invalid username or password")
//                return@setOnClickListener
//            }
//            val users = application as UserApplication
//
//            users.username = user.username
//            users.password = user.password
//            users.email = user.email
//            users.phone = user.phone

            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            db.collection("users").whereEqualTo("email", email).get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        toast("Account not found.")
                    } else {
                        firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    startActivity(Intent(this, HomeActivity::class.java))
                                } else {
                                    val message = when (it.exception) {
                                        is FirebaseAuthInvalidCredentialsException -> "Incorrect password."
                                        is FirebaseNetworkException -> "Network error. Try again."
                                        else -> "Login failed. Please try again."
                                    }
                                    toast(message)
                                }
                            }
                    }
                }
                .addOnFailureListener {
                    toast("Login failed: ${it.message}")
                }


        }

        tvRegisterLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val forgot = findViewById<TextView>(R.id.forgot_pass)
        forgot.setOnClickListener {
            val resetPasswordBottomSheet = ResetPasswordBottomSheet()
            resetPasswordBottomSheet.show(supportFragmentManager, resetPasswordBottomSheet.tag)

        }
    }

}