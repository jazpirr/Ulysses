package cit.edu.ulysses.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.roundToInt

class ScreenTimeFragment : BaseBarChartFragment() {
    private var screenTimes: List<Long> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            screenTimes = it.getLongArray(ARG_SCREEN_TIMES)?.toList() ?: emptyList()
        }
    }

    override fun getBarEntries(): List<BarEntry> {
        return screenTimes.mapIndexed { index, time ->
            val hours = (time/(1000*60*60).toFloat())
            println("Time: ${hours}")
            BarEntry(index.toFloat(), hours)
        }
    }


    override fun getLabelFormatter(): ValueFormatter {
        return object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "${value.roundToInt()}hr"
            }
        }
    }

    override fun getYAxisMax(entries: List<BarEntry>): Float {
        val maxEntry = entries.maxOfOrNull { it.y } ?: 0f
        return maxEntry + 1
    }

    override fun getChartTitle(): String = "Screen time"

    override fun getAxisValueFormatter(): ValueFormatter {
        return IndexAxisValueFormatter(listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"))
    }

    companion object {
        private const val ARG_SCREEN_TIMES = "screen_times"

        fun newInstance(screenTimes: List<Long>) = ScreenTimeFragment().apply {
            arguments = Bundle().apply {
                putLongArray(ARG_SCREEN_TIMES, screenTimes.toLongArray())
            }
        }
    }
}
