package cit.edu.ulysses.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import cit.edu.ulysses.R
import cit.edu.ulysses.app.UserApplication
import cit.edu.ulysses.utils.clearOnFocus
import cit.edu.ulysses.utils.toText

class EditProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile)

        val saveButton = findViewById<Button>(R.id.button_save)
        val pass = findViewById<EditText>(R.id.password_edit)
        val username = findViewById<EditText>(R.id.username_edit)
        val email = findViewById<EditText>(R.id.email_edit)
        val phone = findViewById<EditText>(R.id.number_edit)
        val dob = findViewById<EditText>(R.id.bday_edit)

        pass.setText("*".repeat((application as UserApplication).password.length))
        username.setText((application as UserApplication).username)
        email.setText((application as UserApplication).email)
        phone.setText((application as UserApplication).phone)
        dob.setText((application as UserApplication).dob)

        pass.clearOnFocus()
        username.clearOnFocus()
        email.clearOnFocus()
        phone.clearOnFocus()
        dob.clearOnFocus()


        saveButton.setOnClickListener {
            (application as UserApplication).username = username.toText()
            (application as UserApplication).email = email.toText()
            (application as UserApplication).password = pass.toText()
            (application as UserApplication).phone = phone.toText()
            (application as UserApplication).dob = dob.toText()

            startActivity(
                Intent(this, ProfileActivity::class.java)
            )
        }

        val button_cancal = findViewById<Button>(R.id.button_cancel)
        button_cancal.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
