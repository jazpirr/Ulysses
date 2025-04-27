package cit.edu.ulysses.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import cit.edu.ulysses.fragment.AddNoteFragment
import cit.edu.ulysses.fragment.HomeFragment
import cit.edu.ulysses.fragment.NotesFragment
import cit.edu.ulysses.fragment.SettingsFragment
import cit.edu.ulysses.fragment.TimeoutFragment
import cit.edu.ulysses.Note.NotesAdapter
import cit.edu.ulysses.Note.NotesHelper
import cit.edu.ulysses.R
import cit.edu.ulysses.databinding.ActivityHomeBinding
import cit.edu.ulysses.helpers.PermissionHelper
import com.google.android.material.navigation.NavigationView

class FragmentActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityHomeBinding
    private lateinit var db: NotesHelper
    private lateinit var notesAdapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigation.background = null
        PermissionHelper.checkAndRequestPermissions(this)
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.nav_home -> {
                    openFragment(HomeFragment())
                }
                R.id.nav_timeout -> {
                    openFragment(TimeoutFragment())
                }
//                R.id.nav_accountability -> {
//                    Toast.makeText(this, "Opening Accountability", Toast.LENGTH_LONG).show();
//                    openFragment(AccountabilityFragment())
//                }
                R.id.nav_settings -> {
                    openFragment(SettingsFragment())
                }
                R.id.nav_notes -> {
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
            val dialog = AddNoteFragment {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
                if (currentFragment is NotesFragment) {
                    currentFragment.refreshNotes()
                }
            }
            dialog.show(supportFragmentManager, "AddNoteDialog")
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
}