package cr.ac.una.controlfinancierocameraleoandarturo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationView
import cr.ac.menufragment.ListControlFinancieroFragment
import cr.ac.menufragment.ListaLugaresFragment
import cr.ac.una.controlfinancierocameraleoandarturo.db.AppDatabase
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(this)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.FOREGROUND_SERVICE
                ), LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            startLocationService()
        }

        // Manejar el Intent
        val locationName = intent.getStringExtra("location_name")
        if (locationName != null) {
            Log.d("MainActivity", "Lugar presionado: $locationName")
            openFragmentWithSearch(locationName)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openFragmentWithSearch(query: String) {
        val fragment = ListControlFinancieroFragment().apply {
            arguments = Bundle().apply {
                putString("search_query", query)
            }
        }
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.home_content, fragment)
            .addToBackStack(null)  // Agrega esta línea
            .commit()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val title: Int
        lateinit var fragment: Fragment
        when (menuItem.itemId) {
            R.id.nav_camera -> {
                title = R.string.menu_camera
                fragment = ListControlFinancieroFragment()
            }

            R.id.nav_gallery -> {
                title = R.string.menu_gallery
                fragment = ListaLugaresFragment()
            }

            R.id.nav_manage -> {
                title = R.string.menu_tools
                fragment = FrecuenteFragment()
            }

            R.id.nav_manage2 -> {
                title = R.string.menu_tools2
                fragment = TopFragment()
            }

            R.id.nav_clear_db -> {
                clearDatabase()
                return true
            }

            else -> throw IllegalArgumentException("Unknown menu item selected")
        }

        reemplazarFragmento(fragment, getString(title))
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    fun reemplazarFragmento(fragment: Fragment, title: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.home_content, fragment)
            .addToBackStack(null)  // Agrega esta línea
            .commit()
        setTitle(title)
    }

    private fun startLocationService() {
        val serviceIntent = Intent(this, LocationService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun clearDatabase() {
        lifecycleScope.launch {
            AppDatabase.clearDatabase(applicationContext)
            Toast.makeText(this@MainActivity, "Database cleared", Toast.LENGTH_SHORT).show()
        }
    }
}
