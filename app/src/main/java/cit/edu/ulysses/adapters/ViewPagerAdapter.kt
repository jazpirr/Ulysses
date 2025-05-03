package cit.edu.ulysses.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

private const val NUM_TABS = 2
class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return cit.edu.ulysses.adapters.NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return cit.edu.ulysses.fragment.TermsFragment()
        }
        return cit.edu.ulysses.fragment.PrivacyFragment()
    }
}