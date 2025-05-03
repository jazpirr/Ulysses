package cit.edu.ulysses.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import cit.edu.ulysses.R
import cit.edu.ulysses.activities.LoginActivity
import cit.edu.ulysses.utils.GifUtils

class IntroFragment3 : Fragment(R.layout.fragment_intro3) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val gifView = view.findViewById<ImageView>(R.id.gifImage)
        // Load GIF from resources
        GifUtils.loadGifFromResource(requireContext(), gifView, R.raw.sunrise)
        
        view.findViewById<Button>(R.id.button_get_started).setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }
}
