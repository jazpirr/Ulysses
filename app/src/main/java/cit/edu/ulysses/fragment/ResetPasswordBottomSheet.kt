package cit.edu.ulysses.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cit.edu.ulysses.R
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth


class ResetPasswordBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reset_password_bottom_sheet, container, false)

        val emailEditText = view.findViewById<EditText>(R.id.etEmail)
        val sendLinkButton = view.findViewById<Button>(R.id.btnSendLink)

        sendLinkButton.setOnClickListener {
            val email = emailEditText.text.toString()
            if (email.isNotEmpty()) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Password reset link sent to $email", Toast.LENGTH_SHORT).show()
                            dismiss()
                        } else {
                            Toast.makeText(context, "Failed to send reset link. Try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(context, "Please enter an email address", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}