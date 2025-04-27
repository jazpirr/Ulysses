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
import cit.edu.ulysses.data.DailyStats
import cit.edu.ulysses.fragment.ScreenTimeFragment.OnBarSelectedListener

class HomeFragment : Fragment(), OnBarSelectedListener {
    private val appList = mutableListOf<AppStats>()
    private lateinit var usageStatsHelper: UsageStatsHelper
    private lateinit var adapter: AppListStatAdapter
    private lateinit var tvTotalTime: TextView
    private var packageNames = listOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Overview"
            toolbar.subtitle = ""
        }

        usageStatsHelper = UsageStatsHelper(requireContext())
        tvTotalTime = view.findViewById(R.id.totalTimeText)
        updateTotalTime()

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)

        val (startTimes, endTimes) = usageStatsHelper.getStartAndEndTimesForWeek()
        val screenTimesPerDay = usageStatsHelper.getTotalScreenTimeForRanges(startTimes, endTimes)

        val chartAdapter = ChartPagerAdapter(requireActivity(), screenTimesPerDay, startTimes, endTimes)
        viewPager.adapter = chartAdapter

        // Set up the bar selection listener for each fragment
        (chartAdapter.createFragment(0) as? ScreenTimeFragment)?.setOnBarSelectedListener(this)

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
        adapter = AppListStatAdapter(appList, requireContext())
        recyclerView.adapter = adapter

        return view
    }

    override fun onBarSelected(stats: DailyStats) {
        // Update total time text
        tvTotalTime.text = usageStatsHelper.formatMilliseconds(stats.screenTime)

        // Update app list with new usage stats
        updateAppList(stats.appUsage)
    }

    private fun updateTotalTime() {
        val totalScreenTime = usageStatsHelper.getTotalScreenTime()
        tvTotalTime.text = usageStatsHelper.formatMilliseconds(totalScreenTime)
    }

    private fun updateAppList(appUsage: Map<String, Long>) {
        val pm: PackageManager = requireContext().packageManager
        appList.clear()

        for (packageName in packageNames) {
            val appInfo = pm.getApplicationInfo(packageName, 0)
            val name = pm.getApplicationLabel(appInfo).toString()
            val icon = pm.getApplicationIcon(appInfo)
            val usageStat = appUsage[packageName] ?: 0
            appList.add(AppStats(name, icon, packageName, usageStat))
        }

        appList.sortByDescending { it.statistic }
        adapter.notifyDataSetChanged()
    }

    private fun loadInstalledApps() {
        val pm: PackageManager = requireContext().packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { appInfo ->
                (appInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) || (pm.getLaunchIntentForPackage(appInfo.packageName) != null)
            }
        
        packageNames = apps.map { it.packageName }
        val appStat = usageStatsHelper.getScreenOnTimesForAppsToday(packageNames)

        for (app in apps) {
            val name = pm.getApplicationLabel(app).toString()
            val icon = pm.getApplicationIcon(app)
            val packageName = app.packageName
            val usageStat = appStat[packageName] ?: 0
            appList.add(AppStats(name, icon, packageName, usageStat))
        }
        appList.sortByDescending { it.statistic }
    }
}
