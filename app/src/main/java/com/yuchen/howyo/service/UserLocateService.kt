package com.yuchen.howyo.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.yuchen.howyo.HowYoApplication
import com.yuchen.howyo.MainActivity
import com.yuchen.howyo.MainViewModel
import com.yuchen.howyo.R
import com.yuchen.howyo.data.Result
import com.yuchen.howyo.data.User
import com.yuchen.howyo.data.source.HowYoRepository
import com.yuchen.howyo.ext.toText
import com.yuchen.howyo.signin.UserManager
import com.yuchen.howyo.util.Logger
import com.yuchen.howyo.util.ServiceLocator.howYoRepository
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class UserLocateService : Service() {

    private var configurationChange = false

    private var serviceRunningInForeground = false

    private val localBinder = LocalBinder()

    private lateinit var notificationManager: NotificationManager

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var locationRequest: LocationRequest

    private lateinit var locationCallback: LocationCallback

    private var currentLocation: Location? = null

    private val howYoRepository = (HowYoApplication.instance).howYoRepository

    private var job = Job()

    private val coroutineScope = CoroutineScope(job + Dispatchers.Main)

    override fun onCreate() {

        Logger.d("service onCreate()")

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create().apply {

            interval = TimeUnit.SECONDS.toMillis(60)

            fastestInterval = TimeUnit.SECONDS.toMillis(30)

            maxWaitTime = TimeUnit.MINUTES.toMillis(2)

            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                currentLocation = locationResult.lastLocation

                val intent = Intent(ACTION_HOW_YO_LOCATION_BROADCAST)
                intent.putExtra(EXTRA_LOCATION, currentLocation)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

                if (serviceRunningInForeground) {
                    notificationManager.notify(
                        NOTIFICATION_ID,
                        generateNotification(currentLocation)
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {

        Logger.d("service onBind()")
        stopForeground(true)
        serviceRunningInForeground = false
        configurationChange = false
        return localBinder
    }

    override fun onRebind(intent: Intent) {
        Logger.d("service onRebind()")
        stopForeground(true)
        serviceRunningInForeground = false
        configurationChange = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {

        Logger.d("service onUnbind")

        if (!configurationChange) {
//            Logger.d("Start foreground service")
//            val notification = generateNotification(currentLocation)
//            startForeground(NOTIFICATION_ID, notification)
//            serviceRunningInForeground = true
        }

        return true
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configurationChange = true
    }

    fun subscribeToLocationUpdates() {

        startService(Intent(applicationContext, UserLocateService::class.java))

        try {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
            )
        } catch (unlikely: SecurityException) {

        }
    }

    fun unsubscribeToLocationUpdates() {

        try {
            val removeTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            removeTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    stopSelf()
                }
            }
        } catch (unlikely: SecurityException) {

        }
    }

    private fun generateNotification(location: Location?): Notification {

        updateUserLocation(location)

        Logger.d("generateNotification()")

        val titleText = getString(R.string.app_name)

        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID, titleText, NotificationManager.IMPORTANCE_LOW
        )

        notificationManager.createNotificationChannel(notificationChannel)

        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(getString(R.string.locating) + location.toText())
            .setBigContentTitle(titleText)

        val launchActivityIntent = Intent(this, MainActivity::class.java)

        val activityPendingIntent = PendingIntent.getActivity(
            this, 0, launchActivityIntent, 0
        )

        val notificationCompatBuilder =
            NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)

        return notificationCompatBuilder
            .setStyle(bigTextStyle)
            .setContentTitle(titleText)
            .setContentText(getString(R.string.locating))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(
                R.drawable.resize_arrow, getString(R.string.back_to_app),
                activityPendingIntent
            )
            .build()
    }

    private fun updateUserLocation(location: Location?) {

        coroutineScope.launch {
            var user: User?

            withContext(Dispatchers.IO) {
                user = getUserResult()
            }

            user?.apply {
                latitude = location?.latitude
                longitude = location?.longitude
            }

            withContext(Dispatchers.IO) {
                user?.let { howYoRepository.updateUser(it) }
            }
        }
    }

    private suspend fun getUserResult(): User {

        var user = User()

        when (val result = UserManager.userId?.let { howYoRepository.getUser(it) }) {
            is Result.Success -> {
                user = result.data
            }
            else -> {
                howYoRepository.signOut()
                UserManager.clear()
            }
        }

        return user
    }

    inner class LocalBinder : Binder() {
        internal val service: UserLocateService
            get() = this@UserLocateService
    }

    companion object {

        private const val PACKAGE_NAME = "com.yuchen.howyo"

        internal const val ACTION_HOW_YO_LOCATION_BROADCAST =
            "$PACKAGE_NAME.action.HOW_YO_LOCATION_BROADCAST"

        internal const val EXTRA_LOCATION = "$PACKAGE_NAME.extra.LOCATION"

        private const val NOTIFICATION_ID = 12345678

        private const val NOTIFICATION_CHANNEL_ID = "how_yo_channel_01"
    }
}