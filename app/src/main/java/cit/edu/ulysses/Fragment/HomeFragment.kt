package cit.edu.ulysses.Fragment

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import cit.edu.ulysses.R

class HomeFragment : Fragment() {
    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val pm: PackageManager = requireContext().packageManager
        val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 } // Exclude system apps
            .map { pm.getApplicationLabel(it).toString() } // Get app names

        val listView = view.findViewById<ListView>(R.id.listview)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, installedApps)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            Toast.makeText(requireContext(), "App: ${installedApps[position]}", Toast.LENGTH_SHORT).show()
        }


        // Inflate the layout for this fragment
//        val appList = listOf("Facebook", "Youtube", "TikTok", "Instagram", "MS Teams", "Discord", "Mobile Legends")
//        val view = inflater.inflate(R.layout.fragment_home, container, false)
//
//        val listview = view.findViewById<ListView>(R.id.listview)
//
//        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,appList)
//
//        listview.adapter = arrayAdapter

//        listview.setOnItemClickListener { _, _, position, _ ->
//            Toast.makeText(requireContext(),"Item $position with data ${appList[position]}", Toast.LENGTH_LONG ).show()
//        }


        return view

    }
}