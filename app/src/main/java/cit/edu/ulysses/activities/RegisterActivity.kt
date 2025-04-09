package cit.edu.ulysses.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import cit.edu.ulysses.R
import cit.edu.ulysses.app.UserApplication
import cit.edu.ulysses.utils.isNotValid
import cit.edu.ulysses.utils.toText
import cit.edu.ulysses.utils.toast
import androidx.core.content.edit

class RegisterActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val username = findViewById<EditText>(R.id.et_username)
        val password = findViewById<EditText>(R.id.et_pass)
        val password2 = findViewById<EditText>(R.id.et_pass2)
        val email = findViewById<EditText>(R.id.et_email)
        val btn_create = findViewById<Button>(R.id.btn_create)

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
            if(username.isNotValid() || password.isNotValid() || password2.isNotValid() || email.isNotValid()){
                toast("All fields must be filled.")
                return@setOnClickListener
            } else {
                if(password.text.toString() != password2.text.toString()){
                    toast("Password does not match.")
                    return@setOnClickListener
                }
//                sharedPref.edit() { putString("username", username.toText()) }
//                sharedPref.edit() { putString("email", email.toText()) }
//                sharedPref.edit() { putString("password", password.toText()) }
                (application as UserApplication).username = username.toText()
                (application as UserApplication).email = email.toText()
                (application as UserApplication).password = password.toText()

                startActivity(
                    Intent(this, LoginActivity::class.java)
                )
            }
        }
    }
}