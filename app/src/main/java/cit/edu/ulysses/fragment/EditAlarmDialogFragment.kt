package cit.edu.ulysses.fragment

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import cit.edu.ulysses.R
import cit.edu.ulysses.data.Alarm
import cit.edu.ulysses.alarm.MathAlarmActivity
import cit.edu.ulysses.databinding.ActivityEditAlarmBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class EditAlarmDialogFragment : DialogFragment() {

    private lateinit var binding: ActivityEditAlarmBinding
    private var calendar: Calendar = Calendar.getInstance()
    private var unit: String = ""
    private var alarmData: Alarm? = null
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityEditAlarmBinding.inflate(inflater, container, false)

        initTimePicker()
        initListener()

        val alarmId = arguments?.getString("alarmId")
        if (alarmId != null) {
            fetchAlarmFromFirestore(alarmId)
        }

        return binding.root
    }

    private fun fetchAlarmFromFirestore(alarmId: String) {
        userId?.let { uid ->
            db.collection("users").document(uid).collection("alarms")
                .document(alarmId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        alarmData = document.toObject(Alarm::class.java)
                        initUpdateUI()
                    }
                }
        }
    }

    private fun initUpdateUI() {
        val alarm = alarmData
        if (alarm != null) {
            binding.timePicker.hour = alarm.Hour.toInt()
            binding.timePicker.minute = alarm.Minute.toInt()
            binding.alarmEditTxt.setText(alarm.Label)
            binding.btnOK.text = "Update"
            binding.btnCancel.text = "Delete"
        }
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun initTimePicker() {
        binding.timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)

            val currentTotalMinute = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 60 +
                    Calendar.getInstance().get(Calendar.MINUTE)
            var totalSelectedMinute = hourOfDay * 60 + minute
            if (totalSelectedMinute < currentTotalMinute) {
                totalSelectedMinute += 1440
            }
            val totalResultMinute = totalSelectedMinute - currentTotalMinute
            val resultHour = totalResultMinute / 60
            val resultMinute = totalResultMinute % 60

            binding.timeCount.text = "Your alarm will ring in $resultHour hr $resultMinute min"
        }
    }

    private fun initListener() {
        binding.btnOK.setOnClickListener {
            val hour = calendar.get(Calendar.HOUR_OF_DAY).toString()
            val minute = String.format("%02d", calendar.get(Calendar.MINUTE))
            val label = binding.alarmEditTxt.text.toString()
            unit = SimpleDateFormat("a", Locale.getDefault()).format(calendar.time)

            if (alarmData == null) {
                val newAlarm = Alarm(null, hour, minute, null, unit, label, true)
                createAlarmInFirestore(newAlarm)
            } else {
                val updatedAlarm = alarmData!!.copy(
                    Hour = hour,
                    Minute = minute,
                    Label = label,
                    Unit = unit,
                    On = true
                )
                updateAlarmInFirestore(updatedAlarm)
            }
        }

        binding.btnCancel.setOnClickListener {
            alarmData?.id?.let {
                deleteAlarmFromFirestore(it)
                Toast.makeText(requireContext(), "Successfully Deleted", Toast.LENGTH_SHORT).show()
                refreshFragmentAlarm()
            }
            dismiss()
        }
    }

    private fun createAlarmInFirestore(alarm: Alarm) {
        userId?.let { uid ->
            db.collection("users").document(uid).collection("alarms")
                .add(alarm)
                .addOnSuccessListener { documentRef ->
                    alarm.id = documentRef.id
                    documentRef.set(alarm)
                    scheduleAlarm(requireContext(), alarm)
                    Toast.makeText(requireContext(), "Successfully Saved", Toast.LENGTH_SHORT).show()
                    refreshFragmentAlarm()
                    dismiss()
                }
        }
    }

    private fun updateAlarmInFirestore(alarm: Alarm) {
        userId?.let { uid ->
            db.collection("users").document(uid).collection("alarms")
                .document(alarm.id!!)
                .set(alarm)
                .addOnSuccessListener {
                    scheduleAlarm(requireContext(), alarm)
                    Toast.makeText(requireContext(), "Successfully Updated", Toast.LENGTH_SHORT).show()
                    refreshFragmentAlarm()
                    dismiss()
                }
        }
    }

    private fun deleteAlarmFromFirestore(alarmId: String) {
        userId?.let { uid ->
            db.collection("users").document(uid).collection("alarms")
                .document(alarmId)
                .delete()
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleAlarm(context: Context, alarm: Alarm) {
        val alarmManager: AlarmManager = context.getSystemService(AlarmManager::class.java)
        val intent = Intent(context, MathAlarmActivity::class.java).apply {
            putExtra("alarmLabel", alarm.Label)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
            addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
            addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)

        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            (alarm.Hour.toInt() * 100) + alarm.Minute.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.Hour.toInt())
            set(Calendar.MINUTE, alarm.Minute.toInt())
            set(Calendar.SECOND, 0)
            
            // If the time has already passed today, set it for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        if (alarm.On) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmCalendar.timeInMillis, pendingIntent)
        } else {
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun refreshFragmentAlarm() {
        val fragment = parentFragmentManager.findFragmentById(R.id.fragment_container)
        if (fragment is FragmentAlarm) {
            fragment.refreshRecyclerView()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.90).toInt(),
            (resources.displayMetrics.heightPixels * 0.8).toInt()
        )
    }
}
