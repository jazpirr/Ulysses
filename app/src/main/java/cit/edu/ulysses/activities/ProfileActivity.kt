package cit.edu.ulysses.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
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
        var btn_home = findViewById<ImageButton>(R.id.btnback)
        var btn_delete = findViewById<Button>(R.id.button_delete)

        tf_username.setText((application as UserApplication).username)
        tf_email.setText((application as UserApplication).email)
        tf_dob.setText((application as UserApplication).dob)
        tf_phone.setText((application as UserApplication).phone)
        tf_pass.setText("*".repeat((application as UserApplication).password.length))

        btn_home.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("openFragment", "settings")
            startActivity(intent)
        }


        val buttonEdit = findViewById<Button>(R.id.button_edit)
        buttonEdit.setOnClickListener {
            val intent = Intent(this, EditProfile::class.java)
            Toast.makeText(this, "Editing Profile", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
        
        btn_delete.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete Account")
            builder.setMessage("Are you sure you want to delete this account?")
            builder.setPositiveButton("Yes") { _, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                Toast.makeText(this, "You have successfully deleted your account", Toast.LENGTH_LONG).show();
                (application as UserApplication).email = ""
                (application as UserApplication).phone = ""
                (application as UserApplication).dob = ""
                (application as UserApplication).username = ""
                (application as UserApplication).password = ""
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            builder.setNegativeButton("Cancel", null)
            builder.show()


        }

    }
}
