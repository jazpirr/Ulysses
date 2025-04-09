package cit.edu.ulysses.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import cit.edu.ulysses.Fragment.AccountabilityFragment
import cit.edu.ulysses.Fragment.AddNoteFragment
import cit.edu.ulysses.Fragment.BlankFragment
import cit.edu.ulysses.Fragment.BlankFragment2
import cit.edu.ulysses.Fragment.HomeFragment
import cit.edu.ulysses.Fragment.NotesFragment
import cit.edu.ulysses.Fragment.SettingsFragment
import cit.edu.ulysses.Fragment.TimeoutFragment
import cit.edu.ulysses.Note.NotesAdapter
import cit.edu.ulysses.Note.NotesHelper
import cit.edu.ulysses.R
import cit.edu.ulysses.databinding.ActivityHomeBinding
import cit.edu.ulysses.helpers.AppUsage
import cit.edu.ulysses.services.UsageStatsService
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityHomeBinding
    private lateinit var db: NotesHelper
    private lateinit var notesAdapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val checkPermission = AppUsage(this);
        checkPermission.checkUsagePermission()

        val serviceIntent = Intent(this, UsageStatsService::class.java)
        startService(serviceIntent)

        binding.bottomNavigation.background = null
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
                R.id.nav_settings -> {
                    Toast.makeText(this, "Opening Settings", Toast.LENGTH_LONG).show();
                    openFragment(SettingsFragment())
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
            Toast.makeText(this, "Adding",Toast.LENGTH_SHORT).show()
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


}