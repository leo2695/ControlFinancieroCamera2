package cr.ac.una.controlfinancierocameraleoandarturo

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import cr.ac.una.controlfinancierocameraleoandarturo.db.AppDatabase
import cr.ac.una.controlfinancierocameraleoandarturo.dao.LugarDAO
import cr.ac.una.controlfinancierocameraleoandarturo.entity.Lugar
import cr.ac.una.controlfinancierocameraleoandarturo.service.PagesService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class LocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var notificationManager: NotificationManager
    private var contNotificacion = 2

    private var lastLatitude: Double? = null
    private var lastLongitude: Double? = null

    private lateinit var lugarDao: LugarDAO
    private val pageService = PagesService()

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Places.initialize(applicationContext, "YOUR_GOOGLE_API_KEY")
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        lugarDao = AppDatabase.getInstance(applicationContext).lugarDao()

        createNotificationChannel()
        this.startForeground(1, createNotification("Service running"))

        requestLocationUpdates()
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            "locationServiceChannel",
            "Location Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(serviceChannel)
    }

    private fun createNotification(message: String): Notification {
        return NotificationCompat.Builder(this, "locationServiceChannel")
            .setContentTitle("Location Service")
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
    }

    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 10000
        ).apply {
            setMinUpdateIntervalMillis(5000)
        }.build()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.locations.forEach { location ->
                val latitude = location.latitude
                val longitude = location.longitude

                if (hasLocationChanged(latitude, longitude)) {
                    lastLatitude = latitude
                    lastLongitude = longitude
                    getPlaceName(latitude, longitude)
                }
            }
        }
    }

    private fun hasLocationChanged(newLatitude: Double, newLongitude: Double): Boolean {
        val threshold = 0.001
        if (lastLatitude == null || lastLongitude == null) {
            return true
        }
        val latDiff = Math.abs(newLatitude - lastLatitude!!)
        val lonDiff = Math.abs(newLongitude - lastLongitude!!)
        return latDiff > threshold || lonDiff > threshold
    }

    @SuppressLint("MissingPermission")
    private fun getPlaceName(latitude: Double, longitude: Double) {
        val placeFields: List<Place.Field> = listOf(Place.Field.NAME)
        val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)
        val placesClient: PlacesClient = Places.createClient(this)

        val placeResponse = placesClient.findCurrentPlace(request)
        placeResponse.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val response = task.result
                val topPlaces = response.placeLikelihoods
                    .sortedByDescending { it.likelihood }
                    .take(1)

                topPlaces.forEach { placeLikelihood ->
                    val placeName = placeLikelihood.place.name
                    if (placeName != null) {
                        val message = "Lugar: $placeName, Probabilidad: ${placeLikelihood.likelihood}"
                        sendNotification(message, placeName)
                        Log.d("LocationService", message)

                        GlobalScope.launch {
                            val wikipediaTitle = buscarEnWikipedia(placeName)
                            registrarLugar(latitude, longitude, wikipediaTitle, placeName)
                        }
                    }
                }
            } else {
                val exception = task.exception
                if (exception is ApiException) {
                    Log.e("LocationService", "Lugar no encontrado: ${exception.statusCode}")
                }
            }
        }
    }

    private suspend fun buscarEnWikipedia(placeName: String): String {
        return try {
            val pages = pageService.apiWikiService.Buscar(placeName).pages
            pages?.firstOrNull()?.title ?: "No encontrado"
        } catch (e: Exception) {
            Log.e("LocationService", "Error al buscar en Wikipedia: ${e.message}")
            "No encontrado"
        }
    }

    private suspend fun registrarLugar(latitude: Double, longitude: Double, wikipediaTitle: String, placeName: String) {
        val coordenadas = "$latitude, $longitude"
        val fechaHora = Date()

        val lugar = Lugar(
            coordenadas = coordenadas,
            fechaHora = fechaHora,
            nombreArticuloWikipedia = wikipediaTitle,
            nombreLugar = placeName
        )

        withContext(Dispatchers.IO) {
            lugarDao.insertLugar(lugar)
            Log.d("LocationService", "Lugar registrado: $lugar")
        }
    }

    private fun sendNotification(message: String, placeName: String) {
        val notificationId = contNotificacion++
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("location_name", placeName)
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            notificationId, // Use notificationId as the requestCode to ensure uniqueness
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, "locationServiceChannel")
            .setContentTitle("Lugar encontrado")
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
