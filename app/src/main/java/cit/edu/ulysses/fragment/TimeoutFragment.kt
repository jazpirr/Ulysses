package cit.edu.ulysses.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import cit.edu.ulysses.R
import cit.edu.ulysses.activities.AppListActivity
import cit.edu.ulysses.activities.ProfileActivity

class TimeoutFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_timeout, container, false)

        val btnAddApps = view.findViewById<Button>(R.id.btn_add_apps)

        btnAddApps.setOnClickListener {
            val intent = Intent(requireContext(), AppListActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}
