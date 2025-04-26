package cit.edu.ulysses.alarm

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cit.edu.ulysses.R
import cit.edu.ulysses.databinding.AdapterAlarmBinding

class AlarmAdapter(
    private val context: Context,
    private var alarmList: List<Alarm>,
    private val alarmEdit: AlarmClickedListener
) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    // Move DatabaseHelper here (initialize once, not for every item)
    private val databaseHelper = DatabaseHelper(context)

    class AlarmViewHolder(val binding: AdapterAlarmBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AdapterAlarmBinding.inflate(layoutInflater, parent, false)
        return AlarmViewHolder(binding)
    }

    override fun getItemCount(): Int = alarmList.size

    fun interface AlarmClickedListener {
        fun onAlarmClick(alarm: Alarm)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarmList[position]

        holder.binding.apply {
            alarmHour.text = alarm.Hour.toString()
            alarmMinute.text = alarm.Minute.toString()
            alarmLabel.text = alarm.Label
            alarmUnit.text = alarm.Unit

            // Set AM/PM image
            imageView.setImageResource(
                if (alarm.Unit == "AM") R.drawable.ic_sun else R.drawable.ic_moon
            )

            // Handle clicking on the alarm item
            listLinear.setOnClickListener {
                alarmEdit.onAlarmClick(alarm)
            }

            // Handle switch toggling
            alarmSwitch.isChecked = alarm.On
            alarmSwitch.setOnCheckedChangeListener { _, isChecked ->
                databaseHelper.updateSwitch(isChecked, alarm)
                alarm.On = isChecked
            }
        }
    }

    fun updateList(newList: List<Alarm>) {
        this.alarmList = newList
        notifyDataSetChanged()
    }
}
