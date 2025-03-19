package cit.edu.ulysses

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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