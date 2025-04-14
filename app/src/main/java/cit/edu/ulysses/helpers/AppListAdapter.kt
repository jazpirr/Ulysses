package cit.edu.ulysses.helpers

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cit.edu.ulysses.R
import cit.edu.ulysses.models.AppData
import androidx.core.content.edit

class AppListAdapter(
    private val appList: List<AppData>,
    private val sharedPref: SharedPreferences
) : RecyclerView.Adapter<AppListAdapter.AppViewHolder>() {

    class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.app_icon)
        val name: TextView = itemView.findViewById(R.id.app_name)
        val checkBox: CheckBox = itemView.findViewById(R.id.app_checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val appData = appList[position]

        holder.name.setText(appData.name)
        holder.icon.setImageDrawable(appData.icon)

        val selectedApps = sharedPref.getStringSet("selected_apps", emptySet()) ?: emptySet()
        holder.checkBox.isChecked = selectedApps.contains(appData.packageName)

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            val updatedSet = selectedApps.toMutableSet()
            if (isChecked) {
                updatedSet.add(appData.packageName)
                println(appData.name + " was selected - package name: " + appData.packageName)
            } else {
                updatedSet.remove(appData.packageName)
            }
            // Save the updated set to SharedPreferences
            println("SET UPDATED")
            sharedPref.edit() { putStringSet("selected_apps", updatedSet)
            }
        }
    }

    override fun getItemCount(): Int {
        return appList.size
    }
}
