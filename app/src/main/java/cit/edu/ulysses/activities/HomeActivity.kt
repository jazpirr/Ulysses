package cit.edu.ulysses.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import cit.edu.ulysses.fragment.AddNoteFragment
import cit.edu.ulysses.fragment.EditAlarmDialogFragment
import cit.edu.ulysses.fragment.FragmentAlarm
import cit.edu.ulysses.fragment.HomeFragment
import cit.edu.ulysses.fragment.NotesFragment
import cit.edu.ulysses.fragment.SettingsFragment
import cit.edu.ulysses.fragment.TimeoutFragment
import cit.edu.ulysses.adapters.NotesAdapter
import cit.edu.ulysses.helpers.NotesHelper
import cit.edu.ulysses.R
import cit.edu.ulysses.databinding.ActivityHomeBinding
import cit.edu.ulysses.helpers.PermissionHelper
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityHomeBinding
    private lateinit var db: NotesHelper
    private lateinit var notesAdapter: NotesAdapter

    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim)}
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim)}

    private var clicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigation.background = null
        PermissionHelper.checkAndRequestPermissions(this)
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.nav_home -> {
                    Toast.makeText(this, "Opening Home", Toast.LENGTH_LONG).show();
                    openFragment(HomeFragment())
                }
                R.id.nav_timeout -> {
                    Toast.makeText(this, "Opening Timeout", Toast.LENGTH_LONG).show();
                    openFragment(TimeoutFragment())
                }
//                R.id.nav_accountability -> {
//                    Toast.makeText(this, "Opening Accountability", Toast.LENGTH_LONG).show();
//                    openFragment(AccountabilityFragment())
//                }
                R.id.nav_alarm -> {
                    Toast.makeText(this, "Opening Alarm", Toast.LENGTH_LONG).show();
                    openFragment(FragmentAlarm())
                }
                R.id.nav_notes -> {
                    Toast.makeText(this,"Opening Notes", Toast.LENGTH_LONG).show()
                    openFragment(NotesFragment())
                }
            }
            true
        }

        fragmentManager = supportFragmentManager
        val fragmentToOpen = intent.getStringExtra("openFragment")
        if (fragmentToOpen == "settings") {
            openFragment(SettingsFragment())
            binding.bottomNavigation.menu.findItem(R.id.nav_settings).isChecked = true
        } else {
            openFragment(HomeFragment())
        }


        db = NotesHelper(this)
        notesAdapter = NotesAdapter(db.getAllNotes(), this, supportFragmentManager)


        binding.add.setOnClickListener{
            onAddButtonClicked()
        }


        binding.addNote.setOnClickListener{
            Toast.makeText(this, "Adding Note",Toast.LENGTH_SHORT).show()
            val dialog = AddNoteFragment {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
                if (currentFragment is NotesFragment) {
                    currentFragment.refreshNotes()
                }
            }
            dialog.show(supportFragmentManager, "AddNoteDialog")
        }
        binding.addAlarm.setOnClickListener{
            Toast.makeText(this, "Adding Alarm",Toast.LENGTH_SHORT).show()
            val fragment = EditAlarmDialogFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.show(supportFragmentManager, "edit_alarm")
        }
        notesAdapter.refreshData(db.getAllNotes())
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return true;
    }

    private fun openFragment(fragment: Fragment){
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PermissionHelper.OVERLAY_PERMISSION_REQUEST_CODE -> {
                if (PermissionHelper.isOverlayPermissionGranted(this)) {
                    Toast.makeText(this, "Overlay permission granted", Toast.LENGTH_SHORT).show()
                    println("Overlay permission granted")
                } else {
                    Toast.makeText(this, "Overlay permission not granted", Toast.LENGTH_SHORT).show()
                }

            }

            PermissionHelper.ACCESSIBILITY_PERMISSION_REQUEST_CODE -> {
                if (PermissionHelper.isAccessibilityServiceEnabled(this)) {
                    Toast.makeText(this, "Accessibility permission granted", Toast.LENGTH_SHORT).show()
                    println("Accessibility permission granted")
                } else {
                    Toast.makeText(this, "Accessibility permission not granted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked
    }

    private fun setAnimation(clicked: Boolean) {
        if(!clicked){
            binding.addNote.startAnimation(fromBottom)
            binding.addAlarm.startAnimation(fromBottom)
            binding.add.animate().rotationBy(45f).setDuration(300).start()

        } else {
            binding.add.animate().rotationBy(45f).setDuration(300).start()
            binding.addNote.startAnimation(toBottom)
            binding.addAlarm.startAnimation(toBottom)

        }
    }

    private fun setVisibility(clicked: Boolean) {
        if(!clicked){
            binding.addNote.visibility = View.VISIBLE
            binding.addAlarm.visibility = View.VISIBLE

        } else {
            binding.addNote.visibility = View.INVISIBLE
            binding.addAlarm.visibility = View.INVISIBLE

        }

    }
    private fun setClickable(clicked: Boolean){
        if(!clicked){
            binding.addNote.isClickable = true
            binding.addAlarm.isClickable = true
        } else {
            binding.addNote.isClickable = false
            binding.addAlarm.isClickable = false
        }
    }

}