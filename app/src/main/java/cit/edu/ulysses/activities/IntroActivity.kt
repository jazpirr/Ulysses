package cit.edu.ulysses.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.viewpager2.widget.ViewPager2
import cit.edu.ulysses.R
import cit.edu.ulysses.adapters.IntroPagerAdapter
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val dotsIndicator = findViewById<WormDotsIndicator>(R.id.dotsIndicator)

        val adapter = IntroPagerAdapter(this)
        viewPager.adapter = adapter
        dotsIndicator.attachTo(viewPager)

        //If seen before once, never show again
        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        if (prefs.getBoolean("intro_seen", false)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        prefs.edit() { putBoolean("intro_seen", true) }

    }
}
