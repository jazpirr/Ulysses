package cit.edu.ulysses.fragment

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import cit.edu.ulysses.R
import cit.edu.ulysses.activities.AppListActivity
import androidx.core.content.edit

class TimeoutFragment : Fragment() {
    private var countdownTimer: CountDownTimer? = null
    private lateinit var settingsLockTv: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_timeout, container, false)

        val btnAddApps = view.findViewById<LinearLayout>(R.id.btn_add)
        val btnAccessTime = view.findViewById<LinearLayout>(R.id.btn_access_time)
        val btnLockDuration = view.findViewById<LinearLayout>(R.id.btn_lock_duration)
        val btnCommit = view.findViewById<Button>(R.id.btn_commit)
        settingsLockTv = view.findViewById<TextView>(R.id.settingsLock)

        val prefs = requireContext().getSharedPreferences("lockPrefs", MODE_PRIVATE)
        val isCommitted = prefs.getBoolean("is_committed", false)
        val lockUntil = prefs.getLong("lock_end_time", 0L)

        if (isCommitted && System.currentTimeMillis() < lockUntil) {
            disableAllButtons(btnAddApps, btnAccessTime, btnLockDuration, btnCommit)
            startCountdown(lockUntil - System.currentTimeMillis())
            settingsLockTv.visibility = View.VISIBLE
        } else {
            prefs.edit {
                putBoolean("is_committed", false)
                putLong("lock_end_time", 0L)
            }
            settingsLockTv.visibility = View.GONE
            enableAllButtons(btnAddApps, btnAccessTime, btnLockDuration, btnCommit)
        }

        btnAccessTime.setOnClickListener {
            val dialog = EditAccessTimeDialogFragment { hours, minutes ->
                val seconds = (hours * 60 + minutes) * 60
                prefs.edit {
                    putInt("timer_duration", seconds)
                }
                Toast.makeText(
                    requireContext(),
                    "Access time set to $hours hours and $minutes minutes",
                    Toast.LENGTH_SHORT
                ).show()
            }
            dialog.show(childFragmentManager, "edit_access_time_dialog")
        }

        btnLockDuration.setOnClickListener {
            val dialog = LockDurationDialogFragment { selectedDate ->
                prefs.edit {
                    putLong("lock_duration", selectedDate)
                }
            }
            dialog.show(childFragmentManager, "edit_lock_duration_dialog")
        }

        btnAddApps.setOnClickListener {
            val intent = Intent(requireContext(), AppListActivity::class.java)
            startActivity(intent)
        }

        btnCommit.setOnClickListener {
            CommitDialogFragment {
                val lockUntilTime = prefs.getLong("lock_duration", 0L)
                prefs.edit {
                    putBoolean("is_committed", true)
                    putLong("lock_end_time", lockUntilTime)
                }
                Toast.makeText(requireContext(), "Settings committed!", Toast.LENGTH_SHORT).show()
                disableAllButtons(btnAddApps, btnAccessTime, btnLockDuration, btnCommit)
                startCountdown(lockUntilTime - System.currentTimeMillis())
                settingsLockTv.visibility = View.VISIBLE
            }.show(childFragmentManager, "CommitDialog")
        }

        return view
    }

    private fun startCountdown(durationMillis: Long) {
        countdownTimer?.cancel()

        countdownTimer = object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hours = millisUntilFinished / (1000 * 60 * 60)
                val minutes = (millisUntilFinished / (1000 * 60)) % 60
                val seconds = (millisUntilFinished / 1000) % 60
                settingsLockTv.text = String.format(
                    "Lock ends in: %02d:%02d:%02d",
                    hours, minutes, seconds
                )
            }

            override fun onFinish() {
                settingsLockTv.visibility = View.GONE
                val prefs = requireContext().getSharedPreferences("lockPrefs", MODE_PRIVATE)
                prefs.edit {
                    putBoolean("is_committed", false)
                    putLong("lock_end_time", 0L)
                }
                view?.let { rootView ->
                    val btnAddApps = rootView.findViewById<LinearLayout>(R.id.btn_add)
                    val btnAccessTime = rootView.findViewById<LinearLayout>(R.id.btn_access_time)
                    val btnLockDuration = rootView.findViewById<LinearLayout>(R.id.btn_lock_duration)
                    val btnCommit = rootView.findViewById<Button>(R.id.btn_commit)
                    enableAllButtons(btnAddApps, btnAccessTime, btnLockDuration, btnCommit)
                }
            }
        }.start()
    }

    private fun disableAllButtons(
        btnAddApps: LinearLayout,
        btnAccessTime: LinearLayout,
        btnLockDuration: LinearLayout,
        btnCommit: Button
    ) {
        btnAddApps.isEnabled = false
        btnAccessTime.isEnabled = false
        btnLockDuration.isEnabled = false
        btnCommit.isEnabled = false
    }

    private fun enableAllButtons(
        btnAddApps: LinearLayout,
        btnAccessTime: LinearLayout,
        btnLockDuration: LinearLayout,
        btnCommit: Button
    ) {
        btnAddApps.isEnabled = true
        btnAccessTime.isEnabled = true
        btnLockDuration.isEnabled = true
        btnCommit.isEnabled = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countdownTimer?.cancel()
        countdownTimer = null
    }
}
