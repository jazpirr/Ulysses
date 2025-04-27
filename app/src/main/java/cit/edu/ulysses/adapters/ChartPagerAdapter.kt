package cit.edu.ulysses.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import cit.edu.ulysses.fragment.ScreenTimeFragment

class ChartPagerAdapter(
    activity: FragmentActivity,
    private val screenTimes: List<Long>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ScreenTimeFragment.newInstance("Screen Time", screenTimes)
            1 -> ScreenTimeFragment.newInstance("Notifications", emptyList())
            2 -> ScreenTimeFragment.newInstance("Unlocks", emptyList())
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}
