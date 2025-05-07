package cit.edu.ulysses.alarm

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import cit.edu.ulysses.R
import kotlin.random.Random

class MathAlarmActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private var correctAnswers = 0
    private val totalProblems = 3
    private var currentNum1 = 0
    private var currentNum2 = 0
    private var currentOperation = "+"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_math_alarm)

        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound)
        mediaPlayer.isLooping = true
        mediaPlayer.start()

        // Prevent back press
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        })

        generateAndDisplayProblem()
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        Toast.makeText(this, "You must solve the problems!", Toast.LENGTH_SHORT).show()

        val intent = intent
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        window.decorView.postDelayed({
            startActivity(intent)
        }, 500)
    }

    private fun generateAndDisplayProblem() {
        val random = Random(System.currentTimeMillis())
        currentOperation = listOf("+", "-", "×").random()

        when (currentOperation) {
            "+" -> {
                currentNum1 = random.nextInt(1, 100)
                currentNum2 = random.nextInt(1, 150)
            }
            "-" -> {
                val a = random.nextInt(1, 50)
                val b = random.nextInt(1, 50)
                currentNum1 = maxOf(a, b)
                currentNum2 = minOf(a, b)
            }
            "×" -> {
                currentNum1 = random.nextInt(1, 15)
                currentNum2 = random.nextInt(1, 15)
            }
        }

        val problemText = findViewById<TextView>(R.id.problemText)
        problemText.text = "$currentNum1 $currentOperation $currentNum2 = ?"
    }


    fun checkAnswer(view: android.view.View) {
        val answerInput = findViewById<EditText>(R.id.answerInput)
        val answer = answerInput.text.toString().toIntOrNull()

        if (answer == null) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            return
        }

        val correctAnswer = when (currentOperation) {
            "+" -> currentNum1 + currentNum2
            "-" -> currentNum1 - currentNum2
            "×" -> currentNum1 * currentNum2
            else -> 0
        }

        if (answer == correctAnswer) {
            correctAnswers++
            Toast.makeText(this, "Correct! ($correctAnswers / $totalProblems)", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Wrong! Try again", Toast.LENGTH_SHORT).show()
        }

        answerInput.text.clear()

        if (correctAnswers >= totalProblems) {
            mediaPlayer.stop()
            mediaPlayer.release()
            setResult(Activity.RESULT_OK)
            finish()
        } else if (answer == correctAnswer) {
            generateAndDisplayProblem()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (::mediaPlayer.isInitialized) {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }
                mediaPlayer.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
