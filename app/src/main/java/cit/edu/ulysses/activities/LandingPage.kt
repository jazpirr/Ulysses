package cit.edu.ulysses.activities

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import cit.edu.ulysses.R
import com.google.android.material.navigation.NavigationView


open class LandingPage : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private var saved_username: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.landing_page)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        val textview_welcome_message = findViewById<TextView>(R.id.welcome)
        val navigationView: NavigationView = findViewById(R.id.nav_menu)
        val headerView = navigationView.getHeaderView(0)
        val textview_nav_name = headerView.findViewById<TextView>(R.id.nav_name)




        intent?.let {
            it.getStringExtra("username")?.let { username ->
                textview_welcome_message.setText("Hello $username!")
                saved_username = username
                textview_nav_name.setText(saved_username)

            }
        }

        //textview_welcome_message.setText("Hello $saved_username!")

        drawerLayout = findViewById(R.id.drawerLayout)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeButtonEnabled(false)

        val menuButton: ImageButton = findViewById(R.id.menu_button)
        menuButton.setOnClickListener  {
            if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                drawerLayout.closeDrawer(Gravity.RIGHT)
            } else {
                drawerLayout.openDrawer(Gravity.RIGHT)
            }
        }



        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Opening Home", Toast.LENGTH_LONG).show();

                }
                R.id.nav_profile -> {
                    Toast.makeText(this, "Opening Profile", Toast.LENGTH_LONG).show();

                    startActivity(Intent(this, ProfileActivity::class.java).apply {
                        putExtra("username",saved_username)
                    })

                }
                R.id.nav_settings -> {
                    Toast.makeText(this, "Opening Settings", Toast.LENGTH_LONG).show();

                    startActivity(Intent(this, SettingsActivity::class.java).apply {
                        putExtra("username",saved_username)
                    })
                }

            }
            drawerLayout.closeDrawer(GravityCompat.END)
            true
        }
        val breakdown : Button = findViewById(R.id.breakdown)
        val time_out : RelativeLayout = findViewById(R.id.time_out)
        val notes : RelativeLayout = findViewById(R.id.notes)
        val cloud : RelativeLayout = findViewById(R.id.cloud)
        val accountabilit_partner : RelativeLayout = findViewById(R.id.partner)

        breakdown.setOnClickListener{
            Toast.makeText(this, "Opening Breakdown", Toast.LENGTH_LONG).show();
        }
        time_out.setOnClickListener{
            Toast.makeText(this, "Opening Time out", Toast.LENGTH_LONG).show();
        }
        notes.setOnClickListener{
            Toast.makeText(this, "Opening Notes", Toast.LENGTH_LONG).show();
        }
        cloud.setOnClickListener{
            Toast.makeText(this, "Opening Cloud Backup", Toast.LENGTH_LONG).show();
        }
        accountabilit_partner.setOnClickListener{
            Toast.makeText(this, "Opening Accountability Partner", Toast.LENGTH_LONG).show();
        }




    }


}