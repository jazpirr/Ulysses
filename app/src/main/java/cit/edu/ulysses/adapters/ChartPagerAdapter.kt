package cit.edu.ulysses.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import cit.edu.ulysses.fragment.ScreenTimeFragment
import cit.edu.ulysses.fragment.UnlockFragment
import cit.edu.ulysses.fragment.NotificationsFragment

class ChartPagerAdapter(
    activity: FragmentActivity,
    private val screenTimes: List<Long>,
    private val notifications: List<Long>,
    private val unlocks: List<Long>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ScreenTimeFragment.newInstance(screenTimes)
            1 -> NotificationsFragment.newInstance(notifications)
            2 -> UnlockFragment.newInstance(unlocks)
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}
