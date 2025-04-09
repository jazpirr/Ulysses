package cit.edu.ulysses.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import cit.edu.ulysses.R
import cit.edu.ulysses.app.UserApplication
import cit.edu.ulysses.utils.isNotValid
import cit.edu.ulysses.utils.toText
import cit.edu.ulysses.utils.toast

class LoginActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        var etUsername = findViewById<EditText>(R.id.etUsername)
        var etPassword = findViewById<EditText>(R.id.etPassword)
        var btnLogin = findViewById<Button>(R.id.btnLogin)
        var tvRegisterLink = findViewById<TextView>(R.id.tvRegisterLink)

        etUsername.setText((application as UserApplication).username)
        etPassword.setText((application as UserApplication).password)


        btnLogin.setOnClickListener {
            if(etUsername.isNotValid() || etPassword.isNotValid()){
                toast("Username and password cannot be empty")
                return@setOnClickListener
            }
            if(etUsername.toText() != (application as UserApplication).username ||
                etPassword.toText() != (application as UserApplication).password){
                toast("Invalid username or password")
                return@setOnClickListener
            }

            startActivity(
                Intent(this, HomeActivity:: class.java)
            )
        }

        tvRegisterLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }


}