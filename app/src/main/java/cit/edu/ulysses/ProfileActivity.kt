package cit.edu.ulysses

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var editProfileLauncher: androidx.activity.result.ActivityResultLauncher<Intent>
    private var saved_username: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        var text_username = findViewById<TextView>(R.id.text_username)
        intent?.let {
            it.getStringExtra("username")?.let { username ->
                text_username.setText("Hello $username!")
                saved_username = username
            }
        }
        text_username.setText(saved_username)

        editProfileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val name = data.getStringExtra("NAME")
                    val username = data.getStringExtra("USERNAME")
                    val email = data.getStringExtra("EMAIL")
                    val phone = data.getStringExtra("PHONE")
                    val dob = data.getStringExtra("DOB")

                    Log.d("ProfileUpdate", "Data received: Name = $name, Username = $username, Email = $email, Phone = $phone, DOB = $dob")


                    findViewById<TextView>(R.id.name).text = name
                    findViewById<TextView>(R.id.username).text = username
                    findViewById<TextView>(R.id.email).text = email
                    findViewById<TextView>(R.id.email2).text = email
                    findViewById<TextView>(R.id.number).text = phone
                    findViewById<TextView>(R.id.dob).text = dob

                    Toast.makeText(this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show()
                }
            }
        }


        val buttonEdit = findViewById<Button>(R.id.button_edit)
        buttonEdit.setOnClickListener {
            val intent = Intent(this, EditProfile::class.java)
            Toast.makeText(this, "Editing Profile", Toast.LENGTH_SHORT).show()
            startActivity(intent);
        }
        val buttonprev = findViewById<ImageButton>(R.id.prev)
        buttonprev.setOnClickListener {
            val intent = Intent(this, LandingPage::class.java)
            startActivity(intent);
        }
    }
}
