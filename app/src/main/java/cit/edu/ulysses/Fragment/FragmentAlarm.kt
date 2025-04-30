package cit.edu.ulysses.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import cit.edu.ulysses.activities.SettingsActivity
import cit.edu.ulysses.adapters.AlarmAdapter
import cit.edu.ulysses.databinding.FragmentAlarmBinding
import cit.edu.ulysses.data.Alarm
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FragmentAlarm : Fragment() {
    private lateinit var binding: FragmentAlarmBinding
    private lateinit var alarmList: List<Alarm>
    private lateinit var adapter: AlarmAdapter
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

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
        initUI()
        binding.btnSetting.setOnClickListener{
            startActivity(
                Intent(requireContext(), SettingsActivity::class.java)
            )
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initUI()
    }

    private fun initUI() {
        if (!isAdded) return

        if (userId != null) {
            db.collection("users")
                .document(userId)
                .collection("alarms")
                .get()
                .addOnSuccessListener { result ->
                    if (!isAdded) return@addOnSuccessListener

                    alarmList = mutableListOf()
                    for (document in result) {
                        val alarm = document.toObject(Alarm::class.java)
                        alarm.id = document.id
                        (alarmList as MutableList).add(alarm)
                    }

                    adapter = AlarmAdapter(requireContext(), alarmList, alarmEdit = { alarm ->
                        val fragment = EditAlarmDialogFragment()
                        val bundle = Bundle().apply {
                            alarm.id?.let { putString("alarmId", it) }
                        }
                        fragment.arguments = bundle
                        fragment.show(parentFragmentManager, "edit_alarm")
                    })

                    binding.recyclerView.adapter = adapter
                    binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                }
                .addOnFailureListener {
                    if (isAdded) {
                        Toast.makeText(requireContext(),"Failed!", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    fun refreshRecyclerView() {
        if (userId != null) {
            db.collection("users")
                .document(userId)
                .collection("alarms")
                .get()
                .addOnSuccessListener { result ->
                    alarmList = mutableListOf()
                    for (document in result) {
                        val alarm = document.toObject(Alarm::class.java)
                        alarm.id = document.id
                        (alarmList as MutableList).add(alarm)
                    }
                    adapter.updateList(alarmList)
                }
        }
    }
}
