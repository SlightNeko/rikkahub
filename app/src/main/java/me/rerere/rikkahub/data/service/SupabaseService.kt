package me.rerere.rikkahub.data.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * HTTP client for inserting rows into a Supabase table via the REST API.
 *
 * Uses OkHttp for transport and kotlinx.serialization for JSON encoding.
 * Supabase REST API docs: https://supabase.com/docs/guides/api
 */
class SupabaseService(
    private val supabaseUrl: String,
    private val supabaseApiKey: String,
    private val tableName: String = "device_sync"
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    /**
     * Insert a single sync data row into the configured Supabase table.
     * Returns [Result.success] on HTTP 2xx, or [Result.failure] otherwise.
     */
    suspend fun insertRow(data: SupabaseSyncData): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val jsonBody = buildJsonObject(data)
                val bodyString = jsonBody.toString()

                val request = Request.Builder()
                    .url("$supabaseUrl/rest/v1/$tableName")
                    .header("apikey", supabaseApiKey)
                    .header("Authorization", "Bearer $supabaseApiKey")
                    .header("Content-Type", "application/json")
                    .header("Prefer", "return=minimal")
                    .post(bodyString.toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                response.close() // no body needed for minimal return

                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("HTTP ${response.code}: ${response.message}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Build a JSON object with snake_case keys matching the Supabase table columns.
     * The top-level [SupabaseSyncData] fields are already annotated with @SerialName,
     * but nested structures (location sub-fields, arrays) need manual construction
     * to flatten or structure them correctly for the target schema.
     */
    private fun buildJsonObject(data: SupabaseSyncData): JsonObject {
        return buildJsonObject {
            put("timestamp", data.timestamp)
            put("foreground_app", data.foregroundApp)

            // Flatten location sub-fields into top-level columns
            data.location?.let { loc ->
                put("location_latitude", loc.latitude)
                put("location_longitude", loc.longitude)
                if (loc.address.isNotEmpty()) {
                    put("location_address", loc.address)
                }
            }

            // Nest app_usage and notifications as JSON arrays
            if (data.appUsage.isNotEmpty()) {
                putJsonArray("app_usage") {
                    data.appUsage.forEach { usage ->
                        add(buildJsonObject {
                            put("package_name", usage.packageName)
                            put("app_name", usage.appName)
                            put("time_used_minutes", usage.timeUsedMinutes)
                        })
                    }
                }
            }

            if (data.notifications.isNotEmpty()) {
                putJsonArray("notifications") {
                    data.notifications.forEach { notif ->
                        add(buildJsonObject {
                            put("app_name", notif.appName)
                            put("title", notif.title)
                            put("text", notif.text)
                            put("timestamp", notif.timestamp)
                        })
                    }
                }
            }

            if (data.deviceEvent.isNotEmpty()) {
                put("device_event", data.deviceEvent)
            }

            // Nest health data as a JSON object column
            data.health?.let { h ->
                put("health_data", buildJsonObject {
                    put("step_count", h.stepCount)
                    put("heart_rate", h.heartRate)
                    put("sleep_minutes", h.sleepMinutes)
                })
            }
        }
    }
}
