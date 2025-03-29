package cit.edu.ulysses.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import cit.edu.ulysses.Fragment.AccountabilityFragment
import cit.edu.ulysses.Fragment.BlankFragment
import cit.edu.ulysses.Fragment.BlankFragment2
import cit.edu.ulysses.Fragment.HomeFragment
import cit.edu.ulysses.Fragment.SettingsFragment
import cit.edu.ulysses.Fragment.TimeoutFragment
import cit.edu.ulysses.R
import cit.edu.ulysses.databinding.ActivityHomeBinding
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)



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
                R.id.nav_accountability -> {
                    Toast.makeText(this, "Opening Accountability", Toast.LENGTH_LONG).show();
                    openFragment(AccountabilityFragment())
                }
                R.id.nav_settings -> {
                    Toast.makeText(this, "Opening Settings", Toast.LENGTH_LONG).show();
                    openFragment(SettingsFragment())
                }
            }
            true
        }


        fragmentManager = supportFragmentManager
        openFragment(HomeFragment())


        binding.add.setOnClickListener{
            Toast.makeText(this, "Adding",Toast.LENGTH_SHORT).show()
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                Toast.makeText(this, "Opening Home", Toast.LENGTH_LONG).show();
                openFragment(BlankFragment())
            }
            R.id.nav_profile -> {
                Toast.makeText(this, "Opening Profile", Toast.LENGTH_LONG).show();
                openFragment(BlankFragment2())
            }
            R.id.nav_settings -> {
                Toast.makeText(this, "Opening Settings", Toast.LENGTH_LONG).show();
                openFragment(BlankFragment())
            }
            R.id.nav_settings2 -> {
                Toast.makeText(this, "Opening Settings", Toast.LENGTH_LONG).show();
                openFragment(BlankFragment2())
            }

        }
        return true;
    }

    private fun openFragment(fragment: Fragment){
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }
}