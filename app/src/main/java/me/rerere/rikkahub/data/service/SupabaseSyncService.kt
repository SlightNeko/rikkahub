package me.rerere.rikkahub.data.service

import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

private const val TAG = "SupabaseSyncService"

/**
 * Foreground service that periodically collects device data (location, app usage,
 * notifications) and uploads it to a configured Supabase table via [SupabaseService].
 */
class SupabaseSyncService : Service() {

    companion object {
        const val ACTION_SYNC = "me.rerere.rikkahub.action.SUPABASE_SYNC"
        const val INTERVAL_MINUTES = 15L
        const val NOTIFICATION_ID = 3001
        const val SUPABASE_SYNC_CHANNEL_ID = "supabase_sync"

        private const val PREFS_NAME = "rikkahub_integrations"
        private const val KEY_URL = "supabase_url"
        private const val KEY_KEY = "supabase_key"
        private const val KEY_ENABLED = "supabase_enabled"
    }

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var scheduler: ScheduledExecutorService? = null
    private lateinit var notificationBuilder: NotificationCompat.Builder

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // 1. Read config; stop if not enabled or missing credentials
        val enabled = prefs.getBoolean(KEY_ENABLED, false)
        val url = prefs.getString(KEY_URL, null)
        val key = prefs.getString(KEY_KEY, null)

        if (!enabled || url.isNullOrBlank() || key.isNullOrBlank()) {
            Log.w(TAG, "Supabase sync disabled or missing config, stopping service")
            stopSelf()
            return START_NOT_STICKY
        }

        // 2. Start foreground with notification
        startForegroundCompat()
        notificationBuilder = buildSyncNotification(this)

        // 3. Create SupabaseService and run initial collect-and-upload
        val supabaseService = SupabaseService(
            supabaseUrl = url.trimEnd('/'),
            supabaseApiKey = key
        )

        serviceScope.launch {
            collectAndUpload(supabaseService)
        }

        // 4. Schedule periodic sync
        if (scheduler == null || scheduler!!.isShutdown) {
            scheduler = Executors.newSingleThreadScheduledExecutor()
            scheduler!!.scheduleAtFixedRate(
                {
                    serviceScope.launch {
                        collectAndUpload(supabaseService)
                    }
                },
                INTERVAL_MINUTES,
                INTERVAL_MINUTES,
                TimeUnit.MINUTES
            )
        }

        return START_STICKY
    }

    override fun onDestroy() {
        scheduler?.shutdownNow()
        scheduler = null
        serviceScope.cancel()
        super.onDestroy()
    }

    // ------------------------------------------------------------------ Helpers

    private fun startForegroundCompat() {
        val notification = buildSyncNotification(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun buildSyncNotification(context: Context): android.app.Notification {
        val launchIntent = PendingIntent.getActivity(
            context,
            0,
            context.packageManager.getLaunchIntentForPackage(context.packageName),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(context, SUPABASE_SYNC_CHANNEL_ID)
            .setSmallIcon(me.rerere.rikkahub.R.drawable.small_icon)
            .setContentTitle("Rikkahub Sync")
            .setContentText("Syncing data to Supabase…")
            .setContentIntent(launchIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    // ----------------------------------------------------------- Data collection

    private suspend fun collectAndUpload(service: SupabaseService) {
        try {
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())

            // 1. Location (best-effort, requires permission)
            val location = collectLocation()

            // 2. Foreground app (most recent visible package)
            val foregroundApp = collectForegroundApp()

            // 3. App usage stats (last hour)
            val appUsage = collectAppUsage()

            // 4. Notifications — best-effort from the existing listener
            val notifications = emptyList<SupabaseNotificationData>()

            val data = SupabaseSyncData(
                timestamp = timestamp,
                foregroundApp = foregroundApp,
                location = location,
                appUsage = appUsage,
                notifications = notifications,
                deviceEvent = ""
            )

            val result = service.insertRow(data)
            result.onSuccess {
                Log.d(TAG, "Sync uploaded successfully at $timestamp")
            }.onFailure { e ->
                Log.e(TAG, "Sync upload failed: ${e.message}", e)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during data collection", e)
        }
    }

    private fun collectLocation(): SupabaseLocationData? {
        if (!hasLocationPermission()) return null

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var best: android.location.Location? = null
        for (provider in locationManager.getProviders(true)) {
            @Suppress("MissingPermission")
            val loc = locationManager.getLastKnownLocation(provider) ?: continue
            if (best == null || loc.accuracy < best.accuracy) {
                best = loc
            }
        }
        return best?.let {
            SupabaseLocationData(
                latitude = it.latitude,
                longitude = it.longitude
            )
        }
    }

    private fun collectForegroundApp(): String {
        return try {
            val usm = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val now = System.currentTimeMillis()
            val events = usm.queryEvents(now - TimeUnit.MINUTES.toMillis(1), now)
            val event = UsageEvents.Event()
            var lastForegroundPkg = ""

            while (events.hasNextEvent()) {
                events.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND ||
                    event.eventType == UsageEvents.Event.ACTIVITY_RESUMED
                ) {
                    lastForegroundPkg = event.packageName ?: ""
                }
            }
            lastForegroundPkg
        } catch (e: Exception) {
            Log.w(TAG, "Unable to determine foreground app", e)
            ""
        }
    }

    private fun collectAppUsage(): List<SupabaseAppUsageData> {
        return try {
            val usm = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val now = System.currentTimeMillis()
            val start = now - TimeUnit.HOURS.toMillis(1)

            val stats = usm.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                start,
                now
            )

            val pm = packageManager
            stats
                .filter { it.totalTimeInForeground > 0 }
                .sortedByDescending { it.totalTimeInForeground }
                .take(10)
                .map { stat ->
                    SupabaseAppUsageData(
                        packageName = stat.packageName,
                        appName = resolveAppName(pm, stat.packageName),
                        timeUsedMinutes = (stat.totalTimeInForeground / 60_000).toInt()
                    )
                }
        } catch (e: Exception) {
            Log.w(TAG, "Unable to collect app usage", e)
            emptyList()
        }
    }

    private fun resolveAppName(pm: PackageManager, packageName: String): String {
        return try {
            val info = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(info).toString()
        } catch (_: PackageManager.NameNotFoundException) {
            packageName
        }
    }

    private fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
}
