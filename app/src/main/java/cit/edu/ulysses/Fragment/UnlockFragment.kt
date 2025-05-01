package cit.edu.ulysses.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.roundToInt

class UnlockFragment : BaseBarChartFragment() {
    private var unlocks: List<Long> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            unlocks = it.getLongArray(ARG_UNLOCK_NUM)?.toList() ?: emptyList()
        }
    }

    override fun getBarEntries(): List<BarEntry> {
        return unlocks.mapIndexed { index, unlocks ->
            BarEntry(index.toFloat(), unlocks.toFloat())
        }
    }


    override fun getLabelFormatter(): ValueFormatter {
        return object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.roundToInt().toString()
            }
        }
    }

    override fun getYAxisMax(entries: List<BarEntry>): Float {
        val maxEntry = entries.maxOfOrNull { it.y } ?: 0f
        return maxEntry + 1
    }

    override fun getChartTitle(): String = "Unlocks"

    override fun getAxisValueFormatter(): ValueFormatter {
        return IndexAxisValueFormatter(listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"))
    }

    companion object {
        private const val ARG_UNLOCK_NUM = "unlock_nums"

        fun newInstance( unlocks: List<Long>) = UnlockFragment().apply {
            arguments = Bundle().apply {
                putLongArray(ARG_UNLOCK_NUM, unlocks.toLongArray())
            }
        }
    }
}
