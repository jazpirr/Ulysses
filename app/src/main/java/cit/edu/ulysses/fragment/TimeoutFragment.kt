package cit.edu.ulysses.fragment

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import cit.edu.ulysses.R
import cit.edu.ulysses.activities.AppListActivity
import cit.edu.ulysses.activities.ProfileActivity
import androidx.core.content.edit

class TimeoutFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_timeout, container, false)

        val btnAddApps = view.findViewById<LinearLayout>(R.id.btn_add)
        val btnAccessTime = view.findViewById<LinearLayout>(R.id.btn_access_time)
        val btnLockDuration = view.findViewById<LinearLayout>(R.id.btn_lock_duration)


        btnAccessTime.setOnClickListener {
            val dialog = EditAccessTimeDialogFragment { hours, minutes ->
                val seconds = (hours * 60 + minutes) * 60
                val prefs = requireContext().getSharedPreferences("lockPrefs", MODE_PRIVATE)
                prefs.edit {
                    putInt("timer_duration", seconds)
                    apply()
                }
                Toast.makeText(requireContext(), "Access time set to $hours hours and $minutes minutes", Toast.LENGTH_SHORT).show()

            }
            dialog.show(childFragmentManager, "edit_access_time_dialog")
        }

        btnLockDuration.setOnClickListener {
            val dialog = LockDurationDialogFragment { selectedDate ->
                val prefs = requireContext().getSharedPreferences("lockPrefs", MODE_PRIVATE)
                prefs.edit {
                    putLong("lock_duration", selectedDate)
                }
            }
            dialog.show(childFragmentManager, "edit_lock_duration_dialog")
        }


        btnAddApps.setOnClickListener {
            val intent = Intent(requireContext(), AppListActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}
