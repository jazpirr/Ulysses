package cit.edu.ulysses.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import cit.edu.ulysses.helpers.UsageStatsHelper

class ScreenTimeFragment : Fragment() {

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_SCREEN_TIMES = "screen_times"

        fun newInstance(title: String, screenTimes: List<Long>) = ScreenTimeFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_TITLE, title)
                putLongArray(ARG_SCREEN_TIMES, screenTimes.toLongArray())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val context = requireContext()
        val barchartWeekly = BarChart(context)

        val title = arguments?.getString(ARG_TITLE) ?: ""
        val screenTimes = arguments?.getLongArray(ARG_SCREEN_TIMES)?.toList() ?: emptyList()

        // Convert milliseconds to hours for display
        val entries = screenTimes.mapIndexed { index, time ->
            BarEntry(index.toFloat(), (time / (1000 * 60 * 60)).toFloat())
        }

        val maxValue = entries.maxOfOrNull { it.y } ?: 0f

        barchartWeekly.legend.isEnabled = false
        barchartWeekly.description.isEnabled = false
        barchartWeekly.setFitBars(true)

        barchartWeekly.setDrawValueAboveBar(false)
        barchartWeekly.setDrawGridBackground(false)
        barchartWeekly.setPinchZoom(false)
        barchartWeekly.isDoubleTapToZoomEnabled = false
        barchartWeekly.setScaleEnabled(false)

        barchartWeekly.animateY(250)

        barchartWeekly.setDrawGridBackground(true)
        barchartWeekly.setDrawBorders(true)
        barchartWeekly.xAxis.apply {
            setDrawLabels(true)
            position = XAxis.XAxisPosition.BOTTOM
            textColor = Color.DKGRAY
            setGranularity(1f)
            valueFormatter = IndexAxisValueFormatter(listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"))
        }

        barchartWeekly.axisLeft.apply {
            setDrawLabels(true)
            textColor = Color.DKGRAY
            setDrawGridLines(true)
            setDrawZeroLine(false)

            axisMinimum = 0f
            axisMaximum = maxValue + 1
            setLabelCount(4, true)
            setGranularity(1f)

            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${value.toInt()}hr"
                }
            }
        }

        barchartWeekly.axisRight.isEnabled = false

        val barDataSet = BarDataSet(entries, "Weekly Screen Time")
        barDataSet.color = Color.BLACK
        barDataSet.valueTextColor = Color.TRANSPARENT
        barDataSet.valueTextSize = 0f

        val barData = BarData(barDataSet)
        barData.barWidth = 0.5f
        barchartWeekly.data = barData
        barchartWeekly.invalidate()
        return barchartWeekly
    }
}
