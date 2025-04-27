package cit.edu.ulysses.fragment

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import cit.edu.ulysses.R
import cit.edu.ulysses.alarm.Alarm
import cit.edu.ulysses.alarm.AlarmReceiver
import cit.edu.ulysses.alarm.DatabaseHelper
import cit.edu.ulysses.databinding.ActivityEditAlarmBinding
import java.text.SimpleDateFormat
import java.util.Calendar

class EditAlarmDialogFragment : DialogFragment() {

    private lateinit var binding: ActivityEditAlarmBinding
    private var calendar: Calendar = Calendar.getInstance()
    private lateinit var databaseHelper: DatabaseHelper
    private var unit: String = ""
    private lateinit var alarmData: Pair<Int, Alarm?>

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View? {
        binding = ActivityEditAlarmBinding.inflate(inflater, container, false)
        databaseHelper = DatabaseHelper(requireContext())
        alarmData = returnValue()
        initUpdateUI()
        initTimePicker()
        initListener()
        return binding.root
    }

    private fun returnValue(): Pair<Int, Alarm?> {
        val id = arguments?.getInt("alarmId", -1) ?: -1
        val alarm = if (id != -1) {
            databaseHelper.getAlarmById(id)
        } else {
            null
        }
        return Pair(id, alarm)
    }

    private fun initUpdateUI() {
        val (id, alarm) = returnValue()
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
        //Init TimePicker
        binding.timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            val currentTotalMinute =
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 60 + Calendar.getInstance()
                    .get(Calendar.MINUTE)
            var totalSelectedMinute = hourOfDay * 60 + minute
            if (totalSelectedMinute < currentTotalMinute) {
                totalSelectedMinute += 1440
            }
            val totalResultMinute = totalSelectedMinute - currentTotalMinute
            var resultHour = totalResultMinute / 60
            var resultMinute = totalResultMinute % 60
            if (totalResultMinute < 0) {
                resultHour = -resultHour
                resultMinute = -resultMinute
            }
            binding.timeCount.text = "Your alarm will ring in $resultHour hr $resultMinute min"
        }
    }

    private fun initListener() {
        val (id, alarm) = returnValue()
        // Btn OK
        binding.btnOK.setOnClickListener {
            val hour = calendar.get(Calendar.HOUR_OF_DAY).toString()
            val minute = String.format("%02d", calendar.get(Calendar.MINUTE))
            val label = binding.alarmEditTxt.text.toString()
            unit = SimpleDateFormat("a").format(calendar.time)

            // Create Alarm
            if (alarm == null) {
                val alarmCreate = Alarm(id, hour, minute.format("%02d"), null, unit, label, true)
                scheduleAlarm(requireContext(), alarmCreate)
                databaseHelper.createData(alarmCreate)
                Toast.makeText(requireContext(), "Successfully Saved", Toast.LENGTH_SHORT).show()

                refreshFragmentAlarm()

                dismiss()
            }
            // Update
            else if (alarm != null) {
                if (hour != alarm.Hour || minute != alarm.Minute || label != alarm.Label) {
                    val alarmUpdate = Alarm(id, hour, minute.format("%02d"), null, unit, label, true)
                    scheduleAlarm(requireContext(), alarmUpdate)
                    databaseHelper.updateData(alarmUpdate)
                    Toast.makeText(requireContext(), "Successfully Updated", Toast.LENGTH_SHORT).show()

                    refreshFragmentAlarm()

                    dismiss()
                }
            }

        }

        // Btn Cancel
        binding.btnCancel.setOnClickListener {
            val (id, alarm) = returnValue()
            if (alarm == null) {
                // Btn Cancel (For Creating)
                dismiss()
            } else if (alarm != null) {
                databaseHelper.deleteData(id)
                refreshFragmentAlarm()

                Toast.makeText(requireContext(), "Successfully Deleted", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleAlarm(context: Context, alarm: Alarm) {
        val alarmManager: AlarmManager = context.getSystemService(AlarmManager::class.java)
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("alarmLabel", alarm.Label)
        val pendingIndent: PendingIntent = PendingIntent.getBroadcast(
            context,
            ((alarm.Hour).toInt() * 100) + alarm.Minute.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // Setting alarm to ring
        var alarmCalendar: Calendar = Calendar.getInstance()
        alarmCalendar.set(Calendar.HOUR_OF_DAY, alarm.Hour.toInt())
        alarmCalendar.set(Calendar.MINUTE, alarm.Minute.toInt())
        alarmCalendar.set(Calendar.SECOND, 0)
        if (alarm.On) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmCalendar.timeInMillis, pendingIndent)
        } else {
            alarmManager.cancel(pendingIndent)
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
