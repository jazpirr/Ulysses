package cit.edu.ulysses.fragment

import android.animation.ValueAnimator
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import cit.edu.ulysses.R
import cit.edu.ulysses.adapters.AppListStatAdapter
import cit.edu.ulysses.adapters.ChartPagerAdapter
import cit.edu.ulysses.data.AppStats
import cit.edu.ulysses.helpers.UsageStatsHelper
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    private val appList = mutableListOf<AppStats>()
    private lateinit var usageStatsHelper: UsageStatsHelper
    private lateinit var adapter: AppListStatAdapter
    private lateinit var tvTotalTime: TextView
    private lateinit var tvSubtitle: TextView
    private var packageNames = listOf<String>()
    private var currentTotalTimeMillis: Long = 0L
    private var tabPosition = 0
    private var startTime: Long = 0L
    private var endTime: Long = 0L

    override fun onResume() {
        super.onResume()
        updateOverallStats()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        usageStatsHelper = UsageStatsHelper(requireContext())
        startTime = usageStatsHelper.getStartOfDayMillis()
        endTime = usageStatsHelper.getEndOfDayMillis()
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Overview"
            toolbar.subtitle = ""
        }

        tvTotalTime = view.findViewById(R.id.totalTimeText)
        tvSubtitle = view.findViewById(R.id.subtitle)
        updateTextView(usageStatsHelper.getTotalScreenTime(), "screen_time")

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)
        initializeTabLayout(tabLayout, viewPager)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewStatistics)
        initializeRecyclerView(recyclerView)

        return view
    }

    fun updateOverallStats() {
        when (tabPosition) {
            0 -> updateHomeStats("screen_time", startTime, endTime)
            1 -> updateHomeStats("notifications", startTime, endTime)
            2 -> updateHomeStats("unlocks", startTime, endTime)
            else -> updateHomeStats("screen_time", startTime, endTime)
        }
    }

    private fun initializeRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = AppListStatAdapter(appList, requireContext())
        recyclerView.adapter = adapter
        
        // Add item animation
        val itemAnimator = DefaultItemAnimator().apply {
            addDuration = 300
            removeDuration = 300
            moveDuration = 300
            changeDuration = 300
        }
        recyclerView.itemAnimator = itemAnimator
        
        loadInstalledAppsAsync()
    }

    private fun initializeTabLayout(tabLayout: TabLayout, viewPager: ViewPager2) {
        val (startTimes, endTimes) = usageStatsHelper.getStartAndEndTimesForWeek()
        val screenTimesPerDay = usageStatsHelper.getTotalScreenTimeForRanges(startTimes, endTimes)
        val unlocksPerDay = usageStatsHelper.getUnlocksForRanges(startTimes, endTimes)
        val notificationsPerDay = usageStatsHelper.getNotificationsForRanges(startTimes, endTimes)
        val chartAdapter = ChartPagerAdapter(requireActivity(), screenTimesPerDay, notificationsPerDay, unlocksPerDay)
        viewPager.adapter = chartAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Screen Time"
                1 -> "Notifications"
                2 -> "Unlocks"
                else -> ""
            }
        }.attach()

        barChartListenerUpdate(startTimes, endTimes)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    tabPosition = it.position
                    when (it.position) {
                        0 -> updateHomeStats("screen_time", startTime, endTime)
                        1 -> updateHomeStats("notifications", startTime, endTime)
                        2 -> updateHomeStats("unlocks", startTime, endTime)
                    }
                }
            }


            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {
                onTabSelected(tab)
            }
        })
    }


    private fun barChartListenerUpdate(startTimes: List<Long>, endTimes: List<Long>) {
        parentFragmentManager.setFragmentResultListener("bar_selected", this) { _, bundle ->
            val selectedIndex = bundle.getInt("selected_index")
            val selectedStart = startTimes.getOrNull(selectedIndex) ?: return@setFragmentResultListener
            val selectedEnd = endTimes.getOrNull(selectedIndex) ?: return@setFragmentResultListener

            Log.d("BarChart", bundle.getString("result_source").toString())
            updateHomeStats(bundle.getString("result_source").toString(), selectedStart,selectedEnd)
        }
    }

    private fun updateHomeStats(Type: String, startTimes: Long, endTimes: Long) {
        var appStats: Map<String, Long> = emptyMap()
        var newValue = 0L

        when (Type) {
            "screen_time" -> {
                newValue = usageStatsHelper.getTotalScreenTime(startTimes, endTimes)
                appStats = usageStatsHelper.getScreenOnTimesForApps(packageNames, startTimes, endTimes)
            }
            "unlocks" -> {
                appStats = usageStatsHelper.getUnlocks(null, startTimes, endTimes)
                newValue = appStats["phone_unlocks"] ?: 0
            }
            "notifications" -> {
                appStats = usageStatsHelper.getPackageNotificationCount(packageNames,startTimes,endTimes)
                newValue = appStats.values.sum()
            }
            else -> return
        }

        if (newValue > 0) {
            updateTextView(newValue, Type)
            updateAppList(appStats, Type)
        }
    }


    private fun updateTextView(newValue: Long, type: String) {
        val animator = ValueAnimator.ofFloat(currentTotalTimeMillis.toFloat(), newValue.toFloat())
        animator.duration = 500
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { animation ->
            val animatedValue = (animation.animatedValue as Float).toLong()
            when (type) {
                "screen_time" -> {
                    tvSubtitle.text = "Total screen time"
                    tvTotalTime.text = usageStatsHelper.formatMilliseconds(animatedValue)
                }
                "notifications" -> {
                    tvSubtitle.text = "Notifications received"
                    tvTotalTime.text = "$animatedValue"
                }
                "unlocks" -> {
                    tvSubtitle.text = "Screen unlocks"
                    tvTotalTime.text = "$animatedValue"
                }
                else -> animatedValue.toString()
            }

        }
        animator.start()

        currentTotalTimeMillis = newValue
    }


    private fun updateAppList(appUsage: Map<String, Long>, Type: String) {
        lifecycleScope.launch {
            val pm = requireContext().packageManager

            val updatedStats = withContext(Dispatchers.IO) {
                packageNames.mapNotNull { packageName ->
                    try {
                        val appInfo = pm.getApplicationInfo(packageName, 0)
                        val name = pm.getApplicationLabel(appInfo).toString()
                        val icon = pm.getApplicationIcon(appInfo)
                        val usageStat = when(Type) {
                            "screen_time" -> usageStatsHelper.formatMilliseconds(appUsage[packageName] ?: 0L)
                            "unlocks" -> usageStatsHelper.formatUnlocksLongToInt(appUsage[packageName] ?: 0)
                            "notifications" -> usageStatsHelper.formatNotificationCount(appUsage[packageName] ?: 0)
                            else -> "N/A"
                        }
                        AppStats(name, icon, packageName, usageStat)
                    } catch (e: PackageManager.NameNotFoundException) {
                        null
                    }
                }.sortedByDescending {appUsage[it.packageName] ?: 0L }
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
                    val usage = usageStatsHelper.formatMilliseconds(usageStats[it.packageName] ?: 0L)
                    AppStats(name, icon, it.packageName, usage)
                }.sortedByDescending { it.statistic }
            }

            appList.clear()
            appList.addAll(loadedStats)
            adapter.notifyDataSetChanged()
        }
    }
}
