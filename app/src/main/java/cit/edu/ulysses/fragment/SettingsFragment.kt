package cit.edu.ulysses.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import cit.edu.ulysses.activities.DeveloperActivity
import cit.edu.ulysses.activities.LoginActivity
import cit.edu.ulysses.activities.ProfileActivity
import cit.edu.ulysses.R

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)


        val logoutButton = view.findViewById<Button>(R.id.logout_button)
        logoutButton.setOnClickListener {
            showLogoutConfirmation()
        }
        val aboutUs = view.findViewById<Button>(R.id.about_dev)
        aboutUs.setOnClickListener {
            startActivity(Intent(requireContext(), DeveloperActivity::class.java))
        }
        val profile = view.findViewById<Button>(R.id.btn_profile)
        profile.setOnClickListener {
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
        }

        return view
    }

    private fun showLogoutConfirmation() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to log out?")
        builder.setPositiveButton("OK") { _, _ ->
            val intent = Intent(requireContext(), LoginActivity::class.java)
            Toast.makeText(requireContext(), "You have successfully logged out", Toast.LENGTH_LONG).show();
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }


}
