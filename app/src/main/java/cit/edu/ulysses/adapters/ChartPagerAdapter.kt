package cit.edu.ulysses.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import cit.edu.ulysses.fragment.BarChartFragment

class ChartPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BarChartFragment.newInstance("Screen Time")
            1 -> BarChartFragment.newInstance("Notifications")
            2 -> BarChartFragment.newInstance("Unlocks")
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}
