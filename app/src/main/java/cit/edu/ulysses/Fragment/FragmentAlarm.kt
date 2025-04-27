package cit.edu.ulysses.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import cit.edu.ulysses.activities.HomeActivity
import cit.edu.ulysses.activities.SettingsActivity
import cit.edu.ulysses.alarm.Alarm
import cit.edu.ulysses.alarm.AlarmAdapter
import cit.edu.ulysses.alarm.DatabaseHelper
import cit.edu.ulysses.databinding.FragmentAlarmBinding

class FragmentAlarm : Fragment() {
    private lateinit var binding: FragmentAlarmBinding
    private lateinit var alarmList: List<Alarm>
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var adapter: AlarmAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlarmBinding.inflate(inflater, container, false)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        databaseHelper = DatabaseHelper(requireContext())
        initUI()

        binding.btnSetting.setOnClickListener{
            startActivity(
                Intent(requireContext(), SettingsActivity:: class.java)
            )
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initUI() // Refresh alarms when fragment is resumed
    }

    private fun initUI() {
        alarmList = databaseHelper.getAllData()
        adapter = AlarmAdapter(requireContext(), alarmList, alarmEdit = { alarm ->
            val fragment = EditAlarmDialogFragment()
            val bundle = Bundle().apply {
                alarm.id?.let { putInt("alarmId", it) }
            }
            fragment.arguments = bundle
            fragment.show(parentFragmentManager, "edit_alarm")
        })

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    fun refreshRecyclerView() {
        alarmList = databaseHelper.getAllData()
        adapter.updateList(alarmList)
    }
}
