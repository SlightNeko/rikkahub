package me.rerere.rikkahub.data.service

import android.content.Context
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Health data bundle returned by [GadgetbridgeService].
 */
data class HealthDataBundle(
    val steps: List<DailySteps>,
    val heartRate: List<HeartRateSample>,
    val sleep: List<SleepSession>,
    val source: String = "Gadgetbridge",
    val queryTime: Instant = Instant.now()
)

data class DailySteps(
    val date: String,
    val steps: Int
)

data class HeartRateSample(
    val bpm: Int,
    val time: Instant
)

data class SleepSession(
    val start: Instant,
    val end: Instant,
    val durationMinutes: Long,
    val type: String
)

/**
 * Reads health data from Gadgetbridge's SQLite database via Shizuku.
 *
 * Shizuku provides elevated (ADB-level) privileges that allow bypassing
 * Android's app sandbox to read another app's private data directory.
 *
 * Gadgetbridge package: nodomain.freeyourgadget.gadgetbridge
 * Database path: /data/data/nodomain.freeyourgadget.gadgetbridge/databases/Gadgetbridge
 *
 * Tables queried:
 *   - ACTIVITY_SAMPLE: steps (RAW_KIND=1), heart rate (RAW_KIND=2)
 *   - SLEEP_SESSIONS: sleep data
 */
class GadgetbridgeService(private val context: Context) {

    companion object {
        private const val TAG = "GadgetbridgeService"

        /** Target Gadgetbridge package name */
        const val GB_PACKAGE = "nodomain.freeyourgadget.gadgetbridge"

        /** Path to Gadgetbridge's main SQLite database */
        const val DB_PATH = "/data/data/$GB_PACKAGE/databases/Gadgetbridge"

        /** Temp path used to stage a copy of the DB for reading */
        private const val TEMP_DB_PATH = "/data/local/tmp/rikkahub_gb_temp.db"
    }

    // ── Shizuku status ────────────────────────────────────────────────

    /**
     * Whether the Shizuku service is running and reachable.
     */
    fun isShizukuAvailable(): Boolean {
        return try {
            Shizuku.pingBinder()
        } catch (e: Exception) {
            Log.w(TAG, "Shizuku ping failed", e)
            false
        }
    }

    /**
     * Whether this app has been granted Shizuku permission by the user.
     */
    fun hasShizukuPermission(): Boolean {
        return try {
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Request Shizuku permission if not already granted.
     * Returns immediately; the user must approve via the Shizuku app.
     */
    fun requestShizukuPermissionIfNeeded() {
        if (!hasShizukuPermission()) {
            try {
                Shizuku.requestPermission(0)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to request Shizuku permission", e)
            }
        }
    }

    // ── Health data queries ───────────────────────────────────────────

    /**
     * Get today's step count from Gadgetbridge.
     * Returns 0 if data is unavailable.
     */
    suspend fun getTodaySteps(): Int = withContext(Dispatchers.IO) {
        val db = openDatabase() ?: return@withContext 0
        try {
            val todayStart = ZonedDateTime.now(ZoneId.systemDefault())
                .toLocalDate()
                .atStartOfDay(ZoneId.systemDefault())
                .toEpochSecond()

            val cursor = db.rawQuery(
                """SELECT CAST(SUM(RAW_INTENSITY) AS INTEGER)
                   FROM ACTIVITY_SAMPLE
                   WHERE RAW_KIND = 1 AND CAST(TIMESTAMP/1000 AS INTEGER) >= ?""",
                arrayOf(todayStart.toString())
            )
            cursor.use {
                if (it.moveToFirst()) it.getInt(0) else 0
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to query today's steps", e)
            0
        } finally {
            db.close()
        }
    }

    /**
     * Get today's average heart rate from Gadgetbridge.
     * Returns 0 if no data.
     */
    suspend fun getTodayHeartRate(): Int = withContext(Dispatchers.IO) {
        val db = openDatabase() ?: return@withContext 0
        try {
            val todayStartMs = ZonedDateTime.now(ZoneId.systemDefault())
                .toLocalDate()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            val cursor = db.rawQuery(
                """SELECT CAST(AVG(RAW_INTENSITY) AS INTEGER)
                   FROM ACTIVITY_SAMPLE
                   WHERE RAW_KIND = 2 AND TIMESTAMP >= ?""",
                arrayOf(todayStartMs.toString())
            )
            cursor.use {
                if (it.moveToFirst()) it.getInt(0) else 0
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to query today's heart rate", e)
            0
        } finally {
            db.close()
        }
    }

    /**
     * Get today's total sleep duration in minutes.
     * Returns 0 if no sleep data.
     */
    suspend fun getTodaySleep(): Int = withContext(Dispatchers.IO) {
        val db = openDatabase() ?: return@withContext 0
        try {
            val todayStartMs = ZonedDateTime.now(ZoneId.systemDefault())
                .toLocalDate()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            val cursor = db.rawQuery(
                """SELECT CAST(SUM(TIMESTAMP_TO - TIMESTAMP_FROM)/60000 AS INTEGER)
                   FROM SLEEP_SESSIONS
                   WHERE TIMESTAMP_FROM >= ?""",
                arrayOf(todayStartMs.toString())
            )
            cursor.use {
                if (it.moveToFirst()) it.getInt(0) else 0
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to query today's sleep", e)
            0
        } finally {
            db.close()
        }
    }

    /**
     * Get all health data (steps, heart rate, sleep) for the last [days] days.
     */
    suspend fun getAllHealthData(days: Int = 7): HealthDataBundle = withContext(Dispatchers.IO) {
        val db = openDatabase()
        if (db == null) {
            return@withContext HealthDataBundle(
                steps = emptyList(),
                heartRate = emptyList(),
                sleep = emptyList()
            )
        }

        try {
            val steps = querySteps(db, days)
            val heartRate = queryHeartRate(db, days)
            val sleep = querySleep(db, days)
            HealthDataBundle(steps = steps, heartRate = heartRate, sleep = sleep)
        } finally {
            db.close()
        }
    }

    // ── Database access via Shizuku ───────────────────────────────────

    /**
     * Open a readable handle to Gadgetbridge's database.
     *
     * Strategy:
     * 1. Verify Shizuku is available and permission granted.
     * 2. Use Shizuku newProcess() to copy the DB file from
     *    Gadgetbridge's private data dir to a world-readable temp path.
     * 3. Open the copy with Android's SQLiteDatabase in read-only mode.
     *
     * @return An open [SQLiteDatabase] or null on failure.
     */
    private fun openDatabase(): SQLiteDatabase? {
        if (!isShizukuAvailable()) {
            Log.w(TAG, "Shizuku service not available")
            return null
        }
        if (!hasShizukuPermission()) {
            Log.w(TAG, "Shizuku permission not granted")
            return null
        }

        // Copy the DB from Gadgetbridge's sandbox to a world-readable temp location.
        // Shizuku.newProcess() runs commands with shell (ADB) privileges.
        val copySuccess = try {
            val process = Shizuku.newProcess(
                arrayOf(
                    "sh", "-c",
                    "cp '$DB_PATH' '$TEMP_DB_PATH' && " +
                        "cp '${DB_PATH}-shm' '$TEMP_DB_PATH-shm' 2>/dev/null; " +
                        "cp '${DB_PATH}-wal' '$TEMP_DB_PATH-wal' 2>/dev/null; " +
                        "chmod 644 '$TEMP_DB_PATH'*"
                ),
                null,
                null
            )
            process.waitFor()
            process.exitValue() == 0
        } catch (e: Exception) {
            Log.w(TAG, "Failed to copy Gadgetbridge DB via Shizuku", e)
            false
        }

        if (!copySuccess) {
            Log.w(TAG, "Database copy failed")
            return null
        }

        val tempFile = File(TEMP_DB_PATH)
        if (!tempFile.exists() || !tempFile.canRead()) {
            Log.w(TAG, "Temp DB file not accessible: $TEMP_DB_PATH")
            return null
        }

        return try {
            SQLiteDatabase.openDatabase(
                TEMP_DB_PATH,
                null,
                SQLiteDatabase.OPEN_READONLY
            )
        } catch (e: Exception) {
            Log.w(TAG, "Failed to open temp database", e)
            null
        }
    }

    // ── Query helpers ─────────────────────────────────────────────────

    private fun querySteps(db: SQLiteDatabase, days: Int): List<DailySteps> {
        val cutoffSec = ZonedDateTime.now(ZoneId.systemDefault())
            .minusDays(days.toLong())
            .toEpochSecond()

        return try {
            val cursor = db.rawQuery(
                """SELECT CAST(SUM(RAW_INTENSITY) AS INTEGER) as total_steps,
                          date(CAST(TIMESTAMP/1000 AS INTEGER), 'unixepoch') as day
                   FROM ACTIVITY_SAMPLE
                   WHERE RAW_KIND = 1 AND CAST(TIMESTAMP/1000 AS INTEGER) > ?
                   GROUP BY day
                   ORDER BY day DESC
                   LIMIT ?""",
                arrayOf(cutoffSec.toString(), days.toString())
            )
            val results = mutableListOf<DailySteps>()
            cursor.use {
                while (it.moveToNext()) {
                    val day = it.getString(1) ?: continue
                    val steps = it.getInt(0)
                    results.add(DailySteps(date = day, steps = steps))
                }
            }
            results
        } catch (e: Exception) {
            Log.w(TAG, "Steps query failed", e)
            emptyList()
        }
    }

    private fun queryHeartRate(db: SQLiteDatabase, days: Int): List<HeartRateSample> {
        val cutoffMs = ZonedDateTime.now(ZoneId.systemDefault())
            .minusDays(days.toLong())
            .toInstant()
            .toEpochMilli()

        return try {
            val cursor = db.rawQuery(
                """SELECT CAST(RAW_INTENSITY AS INTEGER) as bpm,
                          CAST(TIMESTAMP/1000 AS INTEGER) as ts
                   FROM ACTIVITY_SAMPLE
                   WHERE RAW_KIND = 2 AND TIMESTAMP > ?
                   ORDER BY TIMESTAMP DESC
                   LIMIT 200""",
                arrayOf(cutoffMs.toString())
            )
            val results = mutableListOf<HeartRateSample>()
            cursor.use {
                while (it.moveToNext()) {
                    val bpm = it.getInt(0)
                    if (bpm in 30..250) {
                        results.add(
                            HeartRateSample(
                                bpm = bpm,
                                time = Instant.ofEpochSecond(it.getLong(1))
                            )
                        )
                    }
                }
            }
            results
        } catch (e: Exception) {
            Log.w(TAG, "Heart rate query failed", e)
            emptyList()
        }
    }

    private fun querySleep(db: SQLiteDatabase, days: Int): List<SleepSession> {
        val cutoffMs = ZonedDateTime.now(ZoneId.systemDefault())
            .minusDays(days.toLong())
            .toInstant()
            .toEpochMilli()

        return try {
            val cursor = db.rawQuery(
                """SELECT CAST(TIMESTAMP_FROM/1000 AS INTEGER) as start_ts,
                          CAST(TIMESTAMP_TO/1000 AS INTEGER) as end_ts,
                          SLEEP_TYPE as type
                   FROM SLEEP_SESSIONS
                   WHERE TIMESTAMP_FROM > ?
                   ORDER BY TIMESTAMP_FROM DESC
                   LIMIT 30""",
                arrayOf(cutoffMs.toString())
            )
            val results = mutableListOf<SleepSession>()
            cursor.use {
                while (it.moveToNext()) {
                    val startSec = it.getLong(0)
                    val endSec = it.getLong(1)
                    val durationMin = (endSec - startSec) / 60
                    val typeCode = it.getInt(2)
                    val typeStr = when (typeCode) {
                        0 -> "light"
                        1 -> "deep"
                        2 -> "rem"
                        else -> "unknown"
                    }
                    results.add(
                        SleepSession(
                            start = Instant.ofEpochSecond(startSec),
                            end = Instant.ofEpochSecond(endSec),
                            durationMinutes = durationMin,
                            type = typeStr
                        )
                    )
                }
            }
            results
        } catch (e: Exception) {
            Log.w(TAG, "Sleep query failed", e)
            emptyList()
        }
    }
}
