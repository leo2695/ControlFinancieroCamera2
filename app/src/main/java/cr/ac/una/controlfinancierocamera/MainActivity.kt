package cr.ac.una.controlfinancierocamera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationView
import cr.ac.menufragment.ListControlFinancieroFragment
import android.view.MenuItem
import cr.ac.una.controlfinancierocamera.service.LocationService
const val FOREGROUND_SERVICE_TYPE_LOCATION = "location"



class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1

    }

    lateinit var drawerLayout: DrawerLayout
    private lateinit var fusedLocationClient: FusedLocationProviderClient

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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Solicitar permisos de ubicación
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.FOREGROUND_SERVICE
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            startLocationService()
            obtenerUbicacion()
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
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
            // Otras opciones de menú si es necesario

            else -> throw IllegalArgumentException("menu option not implemented!!")
        }

        reemplazarFragmento(fragment, getString(title))
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun reemplazarFragmento(fragment: Fragment, title: String) {
        supportFragmentManager
            .beginTransaction()
            //.setCustomAnimations(R.anim.bottom_nav_enter, R.anim.bottom_nav_exit)
            .replace(R.id.home_content, fragment)
            .commit()
        setTitle(title)
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
                obtenerUbicacion()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startLocationService() {
        val serviceIntent = Intent(this, LocationService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun obtenerUbicacion() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    // Mostrar notificación con la ubicación
                    mostrarNotificacion("Estás ubicado en:", "${location.latitude}, ${location.longitude}")
                } else {
                    Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener la ubicación: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarNotificacion(titulo: String, mensaje: String) {
        // Aquí debes implementar la lógica para mostrar una notificación en Android
        // Puedes utilizar la clase NotificationManager para crear y mostrar la notificación
    }
}
