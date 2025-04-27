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

class BarChartFragment : Fragment() {

    companion object {
        private const val ARG_TITLE = ""

        fun newInstance(title: String) = BarChartFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_TITLE, title)
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

        val entries = when (title) {
            "Screen Time" -> listOf(BarEntry(0f, 2f), BarEntry(1f, 3f), BarEntry(2f, 4f),BarEntry(3f, 1.5f),BarEntry(4f, 1.5f),BarEntry(5f, 1.5f),BarEntry(6f, 1.5f))
            "Notifications" -> listOf(BarEntry(0f, 10f), BarEntry(1f, 4.7f), BarEntry(2f, 7.5f),BarEntry(3f, 9.5f),BarEntry(4f, 6.5f),BarEntry(5f, 13.5f),BarEntry(6f, 12.5f))
            "Unlocks" -> listOf(BarEntry(0f, 2f), BarEntry(1f, 3f), BarEntry(2f, 1.5f),BarEntry(3f, 1.5f),BarEntry(4f, 1.5f),BarEntry(5f, 1.5f),BarEntry(6f, 1.5f))
            "App Launches" -> listOf(BarEntry(0f, 2f), BarEntry(1f, 3f), BarEntry(2f, 1.5f),BarEntry(3f, 1.5f),BarEntry(4f, 1.5f),BarEntry(5f, 1.5f),BarEntry(6f, 1.5f))
            else -> emptyList()
        }

        val maxValue = entries.maxOf { it.y }

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
            setGranularity(5f)

            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${value.toInt()}hr"
                }
            }
        }


        barchartWeekly.axisRight.isEnabled = false

        val barDataSet = BarDataSet(entries, "Weekly Data")
        barDataSet.color = Color.BLACK
        barDataSet.valueTextColor = Color.TRANSPARENT
        barDataSet.valueTextSize = 0f

        val barData = BarData(barDataSet)
        barchartWeekly.data = barData
        barchartWeekly.invalidate()
        return barchartWeekly
    }
}
