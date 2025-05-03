package cit.edu.ulysses.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cit.edu.ulysses.services.NotificationMonitorService
import cit.edu.ulysses.helpers.UsageStatsHelper
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.util.Calendar

class NotificationsFragment : BaseBarChartFragment() {
    private lateinit var usageStatsHelper: UsageStatsHelper

    companion object {
        fun newInstance(data: List<Long>): NotificationsFragment {
            val fragment = NotificationsFragment()
            val bundle = Bundle()
            bundle.putLongArray("notification_data", data.toLongArray())
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        usageStatsHelper = UsageStatsHelper(requireContext())
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun getBarEntries(): List<BarEntry> {
        val data = arguments?.getLongArray("notification_data") ?: return emptyList()
        return data.mapIndexed { index, value -> BarEntry(index.toFloat(), value.toFloat()) }
    }

    override fun getLabelFormatter(): ValueFormatter {
        return object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }
    }

    override fun getYAxisMax(entries: List<BarEntry>): Float {
        return entries.maxOfOrNull { it.y }?.let { it + (it * 0.1f) } ?: 10f
    }

    override fun getChartTitle(): String = "Notifications"

    override fun getAxisValueFormatter(): ValueFormatter {
            return IndexAxisValueFormatter(listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"))
    }
}
