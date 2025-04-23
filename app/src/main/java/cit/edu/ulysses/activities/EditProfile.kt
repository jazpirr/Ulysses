package cit.edu.ulysses.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import cit.edu.ulysses.R
import cit.edu.ulysses.app.UserApplication
import cit.edu.ulysses.utils.clearOnFocus
import cit.edu.ulysses.utils.toText
import java.text.SimpleDateFormat
import java.util.Locale

class EditProfile : AppCompatActivity() {
    private val calendar = Calendar.getInstance()
    lateinit var formattedDate: String
    lateinit var dob: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile)

        val saveButton = findViewById<Button>(R.id.button_save)
        val pass = findViewById<EditText>(R.id.password_edit)
        val username = findViewById<EditText>(R.id.username_edit)
        val email = findViewById<EditText>(R.id.email_edit)
        val phone = findViewById<EditText>(R.id.number_edit)
        dob = findViewById<EditText>(R.id.bday_edit)
        dob.showSoftInputOnFocus = false
        dob.isFocusable = false


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

        dob.setOnClickListener{
            showDatePicker()
        }


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

    private fun showDatePicker() {
        val dateDialog = DatePickerDialog(this, R.style.Base_Theme_Ulysses,{DatePickerDialog, year: Int, month: Int, day: Int ->
            calendar.set(year, month, day)
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year,month, day)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            formattedDate = dateFormat.format(selectedDate.time)
            dob.setText(formattedDate)
        },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        dateDialog.window?.setBackgroundDrawableResource(android.R.color.black)



        dateDialog.show()
    }
}
