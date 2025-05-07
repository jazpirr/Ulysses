package cit.edu.ulysses.fragment

import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import cit.edu.ulysses.R
import nl.bryanderidder.themedtogglebuttongroup.ThemedButton
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class LockDurationDialogFragment(
    private val onDateSelected: (selectedDateTime: Long) -> Unit
) : DialogFragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var timePicker: TimePicker
    private lateinit var descriptionText: TextView

    private var selectedDateTime: Calendar = Calendar.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_lock_duration, null)

        val prefs = requireContext().getSharedPreferences("lockPrefs", MODE_PRIVATE)

        calendarView = dialogView.findViewById(R.id.calendarView)
        timePicker = dialogView.findViewById(R.id.timePicker)
        descriptionText = dialogView.findViewById(R.id.descriptionText)
        val toggleGroup = dialogView.findViewById<nl.bryanderidder.themedtogglebuttongroup.ThemedToggleButtonGroup>(R.id.durationPresetToggleGroup)

        val lockDurationMillis = prefs.getLong("lock_duration", 0L)
        if (lockDurationMillis > 0) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = lockDurationMillis
            calendarView.date = calendar.timeInMillis
            timePicker.hour = calendar.get(Calendar.HOUR_OF_DAY)
            timePicker.minute = calendar.get(Calendar.MINUTE)
            updateInfoTexts()
        }
        calendarView.minDate = System.currentTimeMillis()

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDateTime.set(Calendar.YEAR, year)
            selectedDateTime.set(Calendar.MONTH, month)
            selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateInfoTexts()
        }

        timePicker.setIs24HourView(true)
        timePicker.setOnTimeChangedListener { _, hour, minute ->
            selectedDateTime.set(Calendar.HOUR_OF_DAY, hour)
            selectedDateTime.set(Calendar.MINUTE, minute)
            selectedDateTime.set(Calendar.SECOND, 0)
            updateInfoTexts()
        }

        toggleGroup.setOnSelectListener { button: ThemedButton ->
            val daysToAdd = when (button.id) {
                R.id.btn1Day -> 1
                R.id.btn3Days -> 3
                R.id.btn1Week -> 7
                R.id.btn1Month -> 30
                else -> 0
            }
            if (daysToAdd > 0) {
                val now = Calendar.getInstance()
                now.add(Calendar.DAY_OF_MONTH, daysToAdd)
                calendarView.date = now.timeInMillis
                selectedDateTime.timeInMillis = now.timeInMillis
                selectedDateTime.set(Calendar.HOUR_OF_DAY, timePicker.hour)
                selectedDateTime.set(Calendar.MINUTE, timePicker.minute)
                selectedDateTime.set(Calendar.SECOND, 0)
                updateInfoTexts()
            }
        }

        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)

        val dialog = builder.create()

        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnConfirm).setOnClickListener {
            if (selectedDateTime.time.before(Date())) {
                Toast.makeText(requireContext(), "Please choose a valid date and time.", Toast.LENGTH_LONG).show()
            } else {
                onDateSelected(selectedDateTime.timeInMillis)
                Toast.makeText(requireContext(), updateToast(), Toast.LENGTH_LONG).show()
                dialog.dismiss()
            }
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        updateInfoTexts()
        return dialog
    }

    private fun updateInfoTexts() {
        val dateFormat = SimpleDateFormat("EEE, MMM d yyyy 'at' HH:mm", Locale.getDefault())
        descriptionText.text = "Lock will end on ${dateFormat.format(selectedDateTime.time)}"
    }

    private fun updateToast(): String {
        val now = Calendar.getInstance()
        val millisDiff = selectedDateTime.timeInMillis - now.timeInMillis
        val days = TimeUnit.MILLISECONDS.toDays(millisDiff)
        val hours = TimeUnit.MILLISECONDS.toHours(millisDiff) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisDiff) % 60

        val durationParts = mutableListOf<String>()
        if (days > 0) durationParts.add("$days ${if (days == 1L) "day" else "days"}")
        if (hours > 0) durationParts.add("$hours ${if (hours == 1L) "hour" else "hours"}")
        if (minutes > 0 || durationParts.isEmpty()) durationParts.add("$minutes ${if (minutes == 1L) "minute" else "minutes"}")

        return "Time until unlock: " + durationParts.joinToString(" and ")
    }
}
