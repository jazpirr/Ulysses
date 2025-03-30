package cit.edu.ulysses.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cit.edu.ulysses.R
import cit.edu.ulysses.app.UserApplication

class ProfileActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        var tf_username = findViewById<TextView>(R.id.text_username)
        var tf_dob = findViewById<TextView>(R.id.dob)
        var tf_email = findViewById<TextView>(R.id.email)
        var tf_phone = findViewById<TextView>(R.id.number)
        var tf_pass = findViewById<TextView>(R.id.pass)

        tf_username.setText((application as UserApplication).username)
        tf_email.setText((application as UserApplication).email)
        tf_dob.setText((application as UserApplication).dob)
        tf_phone.setText((application as UserApplication).phone)
        tf_pass.setText("*".repeat((application as UserApplication).password.length))



        val buttonEdit = findViewById<Button>(R.id.button_edit)
        buttonEdit.setOnClickListener {
            val intent = Intent(this, EditProfile::class.java)
            Toast.makeText(this, "Editing Profile", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }

    }
}
