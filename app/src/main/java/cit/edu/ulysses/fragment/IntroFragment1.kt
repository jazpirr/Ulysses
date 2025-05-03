package cit.edu.ulysses.fragment

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import cit.edu.ulysses.R
import cit.edu.ulysses.utils.GifUtils

class IntroFragment1 : Fragment(R.layout.fragment_intro1) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gifView1 = view.findViewById<ImageView>(R.id.gifImage)
        val gifView2 = view.findViewById<ImageView>(R.id.gifImage1)

        GifUtils.loadGifFromResource(requireContext(), gifView1, R.raw.boat)
        GifUtils.loadGifFromResource(requireContext(), gifView2, R.raw.mermaid)
    }
}
