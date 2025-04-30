package cit.edu.ulysses.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import cit.edu.ulysses.fragment.ScreenTimeFragment
import cit.edu.ulysses.fragment.UnlockFragment

class ChartPagerAdapter(
    activity: FragmentActivity,
    private val screenTimes: List<Long>,
    private val unlocks: List<Long>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ScreenTimeFragment.newInstance("Screen Time", screenTimes)
            1 -> UnlockFragment.newInstance("Notifications", emptyList())
            2 -> UnlockFragment.newInstance("Unlocks", unlocks)
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}
