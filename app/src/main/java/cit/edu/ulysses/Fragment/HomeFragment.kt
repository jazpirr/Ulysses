package cit.edu.ulysses.Fragment

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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val appList = listOf("Facebook", "Youtube", "TikTok", "Instagram", "MS Teams", "Discord", "Mobile Legends")
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val listview = view.findViewById<ListView>(R.id.listview)

        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,appList)

        listview.adapter = arrayAdapter

        listview.setOnItemClickListener { _, _, position, _ ->
            Toast.makeText(requireContext(),"Item $position with data ${appList[position]}", Toast.LENGTH_LONG ).show()
        }
        return view

    }
}