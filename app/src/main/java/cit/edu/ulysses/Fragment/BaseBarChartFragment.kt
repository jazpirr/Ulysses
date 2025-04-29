package cit.edu.ulysses.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlin.collections.Map.Entry


abstract class BaseBarChartFragment : Fragment() {
    protected lateinit var barChart: BarChart

    abstract fun getBarEntries(): List<BarEntry>
    abstract fun getLabelFormatter(): ValueFormatter
    abstract fun getYAxisMax(entries: List<BarEntry>): Float
    abstract fun getChartTitle(): String
    abstract fun getAxisValueFormatter(): ValueFormatter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        barChart = BarChart(requireContext())
        setupChart()
        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(
                e: com.github.mikephil.charting.data.Entry?,
                h: Highlight?
            ) {
                e?.let {
                    val index = e.x.toInt()
                    Log.d("BarChart", "Selected index: $index with value: ${e.y}")
                    val dataSet = barChart.data.getDataSetByIndex(0) as BarDataSet

                    val normalColor = Color.BLACK
                    val selectedColor = Color.DKGRAY

                    val colors = MutableList(dataSet.entryCount) { normalColor }
                    if (index in colors.indices) {
                        colors[index] = selectedColor
                    }
                    val result = Bundle().apply {
                        putInt("selected_index", index)
                    }
                    parentFragmentManager.setFragmentResult("bar_selected", result)
                    dataSet.colors = colors
                    barChart.invalidate()
                }
            }

            override fun onNothingSelected() {
                Log.d("BarChart", "Nothing selected")
            }
        })

        return barChart
    }


    protected fun setupChart() {
        val entries = getBarEntries()
        val maxValue = getYAxisMax(entries)

        barChart.apply {
            legend.isEnabled = false
            description.isEnabled = false
            setFitBars(true)
            setDrawValueAboveBar(false)
            setDrawGridBackground(false)
            setPinchZoom(false)
            isDoubleTapToZoomEnabled = false
            setScaleEnabled(false)
            animateY(250)
            setDrawGridBackground(true)
            setDrawBorders(true)


            xAxis.apply {
                setDrawLabels(true)
                setDrawGridLines(false)
                position = XAxis.XAxisPosition.BOTTOM
                textColor = Color.DKGRAY
                setGranularity(1f)
                valueFormatter = getAxisValueFormatter()
            }

            axisLeft.apply {
                setDrawLabels(true)
                textColor = Color.DKGRAY
                setDrawGridLines(true)
                setDrawZeroLine(false)
                axisMinimum = 0f
                axisMaximum = maxValue
                setLabelCount(4, true)
                isGranularityEnabled = true
                granularity = 1f
                valueFormatter = getLabelFormatter()
            }

            axisRight.isEnabled = false

            val barDataSet = BarDataSet(entries, getChartTitle())
            barDataSet.color = Color.BLACK
            barDataSet.valueTextColor = Color.TRANSPARENT
            barDataSet.valueTextSize = 0f

            val barData = BarData(barDataSet)
            barData.barWidth = 0.7f
            data = barData
            invalidate()
        }
    }


}
