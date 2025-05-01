package cit.edu.ulysses.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cit.edu.ulysses.R
import cit.edu.ulysses.data.AppStats
import cit.edu.ulysses.helpers.UsageStatsHelper

class AppListStatAdapter(
    private val appList: List<AppStats>,
    context: Context
) : RecyclerView.Adapter<AppListStatAdapter.AppViewHolder>() {
    class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.app_icon)
        val name: TextView = itemView.findViewById(R.id.app_name)
        val stats: TextView = itemView.findViewById(R.id.app_stat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app_stats, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val appStat = appList[position]

        holder.name.text = appStat.name
        holder.stats.text = appStat.statistic
        holder.icon.setImageDrawable(appStat.icon)
    }

    override fun getItemCount(): Int {
        return appList.size
    }
}
