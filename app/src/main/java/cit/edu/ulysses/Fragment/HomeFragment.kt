package cit.edu.ulysses.fragment

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cit.edu.ulysses.R
import cit.edu.ulysses.data.AppStats
import cit.edu.ulysses.adapters.AppListStatAdapter
import cit.edu.ulysses.helpers.UsageStatsHelper
import androidx.viewpager2.widget.ViewPager2
import cit.edu.ulysses.adapters.ChartPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {
    private val appList = mutableListOf<AppStats>()
    private lateinit var usageStatsHelper: UsageStatsHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        usageStatsHelper = UsageStatsHelper(requireContext())

        val tvTotalTime: TextView = view.findViewById(R.id.totalTimeText)
        tvTotalTime.text = usageStatsHelper.formatMilliseconds(usageStatsHelper.getTotalScreenTime())

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Overview"
            toolbar.subtitle = ""
        }

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)

        val adapter = ChartPagerAdapter(requireActivity())
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Screen Time"
                1 -> "Notifications"
                2 -> "Unlocks"
                else -> ""
            }
        }.attach()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewStatistics)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadInstalledApps()
        recyclerView.adapter = AppListStatAdapter(appList, requireContext())

        return view
    }

    private fun loadInstalledApps() {
        val pm: PackageManager = requireContext().packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { appInfo ->
                (appInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) || (pm.getLaunchIntentForPackage(appInfo.packageName) != null)
            }
        val appStat = usageStatsHelper.getScreenOnTimesForAppsToday(apps.map { it.packageName })

        for (app in apps) {
            val name = pm.getApplicationLabel(app).toString()
            val icon = pm.getApplicationIcon(app)
            val packageName = app.packageName
            val usageStat = appStat[packageName] ?: 0
            appList.add(AppStats(name, icon, packageName, usageStat))
        }
        appList.sortByDescending { it.statistic}
    }
}
