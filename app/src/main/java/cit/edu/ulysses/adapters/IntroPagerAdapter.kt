package cit.edu.ulysses.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import cit.edu.ulysses.fragment.IntroFragment1
import cit.edu.ulysses.fragment.IntroFragment2
import cit.edu.ulysses.fragment.IntroFragment3
import cit.edu.ulysses.fragment.IntroFragmentStart

class IntroPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount() = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> IntroFragmentStart()
            1 -> IntroFragment1()
            2 -> IntroFragment2()
            3 -> IntroFragment3()
            else -> throw IllegalStateException("Invalid position")
        }
    }
}
