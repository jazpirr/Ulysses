package cit.edu.ulysses.fragment

import android.animation.ValueAnimator
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
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.get
import kotlin.concurrent.timerTask

class HomeFragment : Fragment() {
    private val appList = mutableListOf<AppStats>()
    private lateinit var usageStatsHelper: UsageStatsHelper
    private lateinit var adapter: AppListStatAdapter
    private lateinit var tvTotalTime: TextView
    private var packageNames = listOf<String>()
    private var currentTotalTimeMillis: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        usageStatsHelper = UsageStatsHelper(requireContext())
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Overview"
            toolbar.subtitle = ""
        }

        tvTotalTime = view.findViewById(R.id.totalTimeText)
        updateTotalTime(usageStatsHelper.getTotalScreenTime())

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)
        initializeTabLayout(tabLayout, viewPager)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewStatistics)
        initializeRecyclerView(recyclerView)

        return view
    }

    private fun initializeRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = AppListStatAdapter(appList, requireContext())
        recyclerView.adapter = adapter
        loadInstalledAppsAsync()
    }

    private fun initializeTabLayout(tabLayout: TabLayout, viewPager: ViewPager2){
        val (startTimes, endTimes) = usageStatsHelper.getStartAndEndTimesForWeek()
        val screenTimesPerDay = usageStatsHelper.getTotalScreenTimeForRanges(startTimes, endTimes)
        val chartAdapter = ChartPagerAdapter(requireActivity(), screenTimesPerDay)
        viewPager.adapter = chartAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Screen Time"
                1 -> "Notifications"
                2 -> "Unlocks"
                else -> ""
            }
        }.attach()

        barChartListenerUpdate(startTimes,endTimes)
    }

    private fun barChartListenerUpdate(startTimes: List<Long>, endTimes: List<Long>) {
        parentFragmentManager.setFragmentResultListener("bar_selected", this) { _, bundle ->
            val selectedIndex = bundle.getInt("selected_index")
            val selectedStart = startTimes[selectedIndex]
            val selectedEnd = endTimes[selectedIndex]

            val newTotalTimeMillis = usageStatsHelper.getTotalScreenTime(selectedStart, selectedEnd)
            if(newTotalTimeMillis >= 0){
                updateTotalTime(newTotalTimeMillis)

                val appUsageForDay = usageStatsHelper.getScreenOnTimesForApps(packageNames, selectedStart, selectedEnd)
                updateAppList(appUsageForDay)
            }
        }
    }

    private fun updateTotalTime(newMillis: Long) {
        val animator = ValueAnimator.ofFloat(currentTotalTimeMillis.toFloat(), newMillis.toFloat())
        animator.duration = 250
        animator.addUpdateListener { animation ->
            val animatedValue = (animation.animatedValue as Float).toLong()
            tvTotalTime.text = usageStatsHelper.formatMilliseconds(animatedValue)
        }
        animator.start()

        currentTotalTimeMillis = newMillis
    }

    private fun updateAppList(appUsage: Map<String, Long>) {
        lifecycleScope.launch {
            val pm = requireContext().packageManager

            val updatedStats = withContext(Dispatchers.IO) {
                packageNames.mapNotNull { packageName ->
                    try {
                        val appInfo = pm.getApplicationInfo(packageName, 0)
                        val name = pm.getApplicationLabel(appInfo).toString()
                        val icon = pm.getApplicationIcon(appInfo)
                        val usageStat = appUsage[packageName] ?: 0
                        AppStats(name, icon, packageName, usageStat)
                    } catch (e: PackageManager.NameNotFoundException) {
                        null
                    }
                }.sortedByDescending { it.statistic }
            }

            appList.clear()
            appList.addAll(updatedStats)
            adapter.notifyDataSetChanged()
        }
    }

    private fun loadInstalledAppsAsync() {
        lifecycleScope.launch {
            val pm = requireContext().packageManager
            val apps = withContext(Dispatchers.IO) {
                pm.getInstalledApplications(PackageManager.GET_META_DATA)
                    .filter {
                        (it.flags and ApplicationInfo.FLAG_SYSTEM == 0) ||
                                pm.getLaunchIntentForPackage(it.packageName) != null
                    }
            }

            packageNames = apps.map { it.packageName }

            val usageStats = withContext(Dispatchers.IO) {
                usageStatsHelper.getScreenOnTimesForApps(
                    packageNames,
                    usageStatsHelper.getStartOfDayMillis(),
                    usageStatsHelper.getEndOfDayMillis()
                )
            }

            val loadedStats = withContext(Dispatchers.IO) {
                apps.map {
                    val name = pm.getApplicationLabel(it).toString()
                    val icon = pm.getApplicationIcon(it)
                    val usage = usageStats[it.packageName] ?: 0L
                    AppStats(name, icon, it.packageName, usage)
                }.sortedByDescending { it.statistic }
            }

            appList.clear()
            appList.addAll(loadedStats)
            adapter.notifyDataSetChanged()
        }
    }
}
