package com.longle.location

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.longle.R
import com.longle.data.db.TimeLineDao
import com.longle.data.model.Timeline
import com.longle.ui.main.MainActivity
import dagger.android.AndroidInjection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class LocationService : Service() {

    @Inject
    lateinit var timeLineDao: TimeLineDao

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mTrackingLocation: Boolean = false
    private var latestTimeline: Timeline? = null
    private var currentBestLocation: Location? = null

    private val locationRequest: LocationRequest
        get() {
            val locationRequest = LocationRequest()
            locationRequest.interval = 30000
            locationRequest.fastestInterval = 5000
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            return locationRequest
        }

    private val locationCallback: LocationCallback
        get() {
            return object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    if (mTrackingLocation) {
                        GlobalScope.launch {
                            val location = locationResult.lastLocation
                            if (isBetterLocation(location, currentBestLocation)) {
                                currentBestLocation = location
                                fetchAddress(location)
                            }
                        }
                    }
                }
            }
        }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Check GPS is enabled
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show()
        }
        // Initialize the FusedLocationClient.
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // Login firebase
        loginToFirebase()

        startForeground(foregroundId, getNotification())
        return START_STICKY
    }

    private fun loginToFirebase() {
        // Authenticate with Firebase, and request location updates
        val email: String = getString(R.string.firebase_email)
        val password: String = getString(R.string.firebase_password)
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("LongLe", "firebase auth success")
                    if (!mTrackingLocation) {
                        startTrackingLocation()
                    }
                } else {
                    Log.d("LongLe", "firebase auth failed")
                }
            }
    }

    private fun startTrackingLocation() {
        mTrackingLocation = true
        mFusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null /* Looper */
        )
    }

    private fun stopTrackingLocation() {
        if (mTrackingLocation) {
            mTrackingLocation = false
            mFusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun getNotification(): Notification {
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("locationService", "LocationService")
        } else ""
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_home_black_24dp)
            .setStyle(NotificationCompat.BigTextStyle().bigText(getString(R.string.notification_msg)))
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    private suspend fun fetchAddress(location: Location) = withContext(Dispatchers.IO) {
        // Set up the geocoder
        val geocoder = Geocoder(this@LocationService, Locale.getDefault())

        // Get the passed in location
        var addresses: List<Address>? = null
        var resultMessage = ""

        try {
            addresses = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            )
        } catch (ioException: IOException) { // Catch network or other I/O problems
            resultMessage = getString(R.string.service_not_available)
            Log.e("LongLe", resultMessage, ioException)
        } catch (illegalArgumentException: IllegalArgumentException) { // Catch invalid latitude or longitude values
            resultMessage = getString(R.string.invalid_lat_long_used)
            Log.e(
                "LongLe", resultMessage + ". " +
                        "Latitude = " + location.latitude +
                        ", Longitude = " +
                        location.longitude, illegalArgumentException
            )
        }

        // If no addresses found, print an error message.
        if (addresses.isNullOrEmpty()) {
            if (resultMessage.isEmpty()) {
                resultMessage = getString(R.string.no_address_found)
                Log.e("LongLe", resultMessage)
            }
        } else { // If an address is found, read it into resultMessage
            val address = addresses[0]
            val addressParts =
                ArrayList<String?>()
            // Fetch the address lines using getAddressLine, join them, and send them to the thread
            for (i in 0..address.maxAddressLineIndex) {
                addressParts.add(address.getAddressLine(i))
            }
            resultMessage = TextUtils.join(
                "\n",
                addressParts
            )
        }

        if (!resultMessage.isNullOrBlank()) {
            val currTime = System.currentTimeMillis()
            val currTimeline = Timeline(
                SimpleDateFormat("dd-MM-YYYY", Locale("vi", "VN")).format(currTime),
                SimpleDateFormat("EEEE", Locale("vi", "VN")).format(currTime),
                "school",
                getString(R.string.no_place),
                resultMessage,
                currTime,
                Long.MAX_VALUE,
                location.latitude,
                location.longitude
            )
            latestTimeline = latestTimeline ?: timeLineDao.getTimeLine()
            if (currTimeline != latestTimeline) {
                val path = getString(R.string.firebase_path) + "/" + getString(R.string.user_id)
                val ref = FirebaseDatabase.getInstance().getReference(path)

                latestTimeline?.run {
                    stayTo = currTimeline.stayFrom
                    timeLineDao.insert(this)
                    // FireBase store
                    ref.setValue(this)
                }
                latestTimeline = currTimeline
                timeLineDao.insert(currTimeline)
                // FireBase store
                ref.setValue(currTimeline)
                // clean up db
                // timeLineDao.cleanUp()
            }
        }
    }

    private fun isBetterLocation(location: Location?, currentBestLocation: Location?): Boolean {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true
        }

        if ((location?.latitude == currentBestLocation.latitude) && location.longitude == currentBestLocation.longitude)
            return false

        // Check whether the new location fix is newer or older
        val timeDelta = location?.time?.minus(currentBestLocation.time) ?: 0
        val isSignificantlyNewer = timeDelta > TWO_MINUTES
        val isSignificantlyOlder = timeDelta < -TWO_MINUTES
        val isNewer = timeDelta > 0

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true
        }
        // If the new location is more than two minutes older, it must be worse
        if (isSignificantlyOlder) {
            return false
        }

        // Check whether the new location fix is more or less accurate
        val accuracyDelta = (location?.accuracy?.minus(currentBestLocation.accuracy))?.toInt() ?: 0
        val isLessAccurate = accuracyDelta > 0
        val isMoreAccurate = accuracyDelta < 0
        val isSignificantlyLessAccurate = accuracyDelta > 200

        // Check if the old and new location are from the same provider
        val isFromSameProvider = isSameProvider(
            location?.provider,
            currentBestLocation.provider
        )

        // Determine location quality using a combination of timeliness and accuracy
        return (isMoreAccurate
                || isNewer && !isLessAccurate
                || isNewer && !isSignificantlyLessAccurate && isFromSameProvider)
    }

    /**
     * Checks whether two providers are the same
     */
    private fun isSameProvider(provider1: String?, provider2: String?): Boolean {
        return if (provider1 == null) {
            provider2 == null
        } else provider1 == provider2
    }

    override fun onDestroy() {
        stopTrackingLocation()
        super.onDestroy()
    }

    companion object {
        private const val foregroundId = 100101102
        private const val TWO_MINUTES = 120000
        private const val FIVE_MINUTES = 300000
    }
}
