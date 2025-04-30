package cit.edu.ulysses.activities

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cit.edu.ulysses.adapters.AppListAdapter
import cit.edu.ulysses.R
import cit.edu.ulysses.models.AppData
import java.util.Locale

class AppListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView ; private lateinit var adapter: AppListAdapter
    private val appList = mutableListOf<AppData>()
    private val filteredAppList = mutableListOf<AppData>()
    private val sharedPref by lazy { getSharedPreferences("appPref", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_list)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Configured Apps"
            setDisplayHomeAsUpEnabled(true)
        }


        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        loadInstalledApps()
        adapter = AppListAdapter(filteredAppList,sharedPref)
        recyclerView.adapter = adapter

        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterApps(newText)
                return true
            }
        })
    }

    private fun loadInstalledApps() {
        val pm: PackageManager = packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA).filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
        for (app in apps) {
                if(app.packageName.equals(packageName)){
                    println(packageName)
                    continue
                }
                val name = pm.getApplicationLabel(app).toString()
                val icon = pm.getApplicationIcon(app)
                val packageName = app.packageName
                println("App name: $name - $packageName")
                appList.add(AppData(name, icon, packageName))
                filteredAppList.add(AppData(name, icon, packageName))
        }
        appList.sortBy { it.name }
        filteredAppList.sortBy { it.name }
    }

    private fun filterApps(query: String?) {
        filteredAppList.clear()

        if (query.isNullOrEmpty()) {
            filteredAppList.addAll(appList)
        } else {
            val filterQuery = query.lowercase(Locale.ROOT)
            for (app in appList) {
                if (app.name.lowercase(Locale.getDefault()).contains(filterQuery)) {
                    filteredAppList.add(app)
                }
            }
        }

        adapter.notifyDataSetChanged()
    }
}
