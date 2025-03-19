package cit.edu.ulysses

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class EditProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile)

        val saveButton = findViewById<Button>(R.id.button_save)

        saveButton.setOnClickListener {
            val name = findViewById<EditText>(R.id.name_edit).text.toString()
            val username = findViewById<EditText>(R.id.username_edit).text.toString()
            val email = findViewById<EditText>(R.id.email_edit).text.toString()
            val phone = findViewById<EditText>(R.id.number_edit).text.toString()
            val dob = findViewById<EditText>(R.id.bday_edit).text.toString()

            val resultIntent = Intent().apply {
                putExtra("NAME", name)
                putExtra("USERNAME", username)
                putExtra("EMAIL", email)
                putExtra("PHONE", phone)
                putExtra("DOB", dob)
            }

            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        val button_prev = findViewById<ImageButton>(R.id.prev)
        button_prev.setOnClickListener {
            val intent = Intent(this, LandingPage::class.java)
            startActivity(intent);
        }

        val button_cancal = findViewById<Button>(R.id.button_cancel)
        button_cancal.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent);
        }
    }
}
