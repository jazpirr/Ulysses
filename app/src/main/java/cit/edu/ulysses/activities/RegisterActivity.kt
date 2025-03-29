package cit.edu.ulysses.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import cit.edu.ulysses.R

class RegisterActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val loginLink = findViewById<TextView>(R.id.haveAccount)
        loginLink.setOnClickListener { v ->
            val intent =
                Intent(
                    this,
                    LoginActivity::class.java
                )
            startActivity(intent)
        }
    }
}