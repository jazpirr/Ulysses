package cit.edu.ulysses.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cit.edu.ulysses.R
import cit.edu.ulysses.data.Alarm
import cit.edu.ulysses.databinding.AdapterAlarmBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AlarmAdapter(
    private val context: Context,
    private var alarmList: List<Alarm>,
    private val alarmEdit: AlarmClickedListener
) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    class AlarmViewHolder(val binding: AdapterAlarmBinding) : RecyclerView.ViewHolder(binding.root)

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

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
        Log.d("AlarmAdapter", "Binding alarm at position $position: $alarm")

        holder.binding.apply {
            alarmHour.text = alarm.Hour
            alarmMinute.text = alarm.Minute
            alarmLabel.text = alarm.Label
            alarmUnit.text = alarm.Unit

            imageView.setImageResource(
                if (alarm.Unit == "AM") R.drawable.ic_sun else R.drawable.ic_moon
            )

            listLinear.setOnClickListener {
                Log.d("AlarmAdapter", "Alarm item clicked: ${alarm.id}")
                alarmEdit.onAlarmClick(alarm)
            }

            alarmSwitch.isChecked = alarm.On
            alarmSwitch.setOnCheckedChangeListener { _, isChecked ->
                Log.d("AlarmAdapter", "Switch toggled for alarm ${alarm.id}: $isChecked")
                updateSwitchStatus(alarm, isChecked)
                alarm.On = isChecked
            }
        }
    }

    private fun updateSwitchStatus(alarm: Alarm, isChecked: Boolean) {
        userId?.let { uid ->
            val id = alarm.id
            if (id == null) {
                Log.e("AlarmAdapter", "Cannot update switch: alarm ID is null")
                return
            }

            db.collection("users").document(uid).collection("alarms").document(id)
                .update("On", isChecked)
                .addOnSuccessListener {
                    Log.d("AlarmAdapter", "Switch status updated in Firestore for alarm ID: $id")
                }
                .addOnFailureListener { e ->
                    Log.e("AlarmAdapter", "Failed to update switch in Firestore: ${e.message}", e)
                }
        }
    }

    fun updateList(newList: List<Alarm>) {
        this.alarmList = newList
        Log.d("AlarmAdapter", "Alarm list updated with ${newList.size} items")
        notifyDataSetChanged()
    }
}
