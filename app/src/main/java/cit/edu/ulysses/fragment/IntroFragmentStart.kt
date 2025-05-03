package cit.edu.ulysses.fragment

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import cit.edu.ulysses.R
import cit.edu.ulysses.utils.GifUtils

class IntroFragmentStart : Fragment(R.layout.fragment_intro_start) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gifView = view.findViewById<ImageView>(R.id.gifImage)
    }
}
