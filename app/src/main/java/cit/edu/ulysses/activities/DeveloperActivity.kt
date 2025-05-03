package cit.edu.ulysses.activities

import android.content.Intent
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import cit.edu.ulysses.R

class DeveloperActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer)

        var btn_back = findViewById<ImageButton>(R.id.btn_back)
        btn_back.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        setupExpandableSection(
            buttonId = R.id.expandBtn,
            layoutId = R.id.expandableLayout,
            cardId = R.id.cardView2
        )

        setupExpandableSection(
            buttonId = R.id.expandBtn2,
            layoutId = R.id.expandableLayout2,
            cardId = R.id.cardView
        )


    }

    private fun setupExpandableSection(buttonId: Int, layoutId: Int, cardId: Int) {
        val button = findViewById<ImageButton>(buttonId)
        val layout = findViewById<LinearLayout>(layoutId)
        val cardView = findViewById<CardView>(cardId)

        button.setOnClickListener {
            if (layout.visibility == View.GONE) {
                layout.visibility = View.VISIBLE
                layout.alpha = 0f
                layout.scaleY = 0f

                TransitionManager.beginDelayedTransition(cardView, AutoTransition().apply {
                    duration = 300
                })

                layout.animate()
                    .alpha(1f)
                    .scaleY(1f)
                    .setDuration(300)
                    .setInterpolator(android.view.animation.DecelerateInterpolator())
                    .start()

                button.setImageResource(R.drawable.baseline_expand_less_24)
            } else {
                TransitionManager.beginDelayedTransition(cardView, AutoTransition().apply {
                    duration = 300
                })

                layout.animate()
                    .alpha(0f)
                    .scaleY(0f)
                    .setDuration(300)
                    .setInterpolator(android.view.animation.AccelerateInterpolator())
                    .withEndAction {
                        layout.visibility = View.GONE
                        layout.alpha = 1f
                        layout.scaleY = 1f
                    }
                    .start()

                button.setImageResource(R.drawable.baseline_expand_more_24)
            }
        }
    }


}
