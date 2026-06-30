package me.rerere.rikkahub.data.service

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * Top-level sync payload sent to Supabase.
 */
@Serializable
data class SupabaseSyncData(
    @SerialName("timestamp")
    val timestamp: String,           // yyyy-MM-dd HH:mm:ss

    @SerialName("foreground_app")
    val foregroundApp: String = "",  // 当前前台应用包名

    @SerialName("location")
    val location: SupabaseLocationData? = null,

    @SerialName("app_usage")
    val appUsage: List<SupabaseAppUsageData> = emptyList(),

    @SerialName("notifications")
    val notifications: List<SupabaseNotificationData> = emptyList(),

    @SerialName("device_event")
    val deviceEvent: String = "",    // 设备事件类型

    @SerialName("health")
    val health: SupabaseHealthData? = null,
)

@Serializable
data class SupabaseLocationData(
    @SerialName("latitude")
    val latitude: Double,

    @SerialName("longitude")
    val longitude: Double,

    @SerialName("address")
    val address: String = "",
)

@Serializable
data class SupabaseAppUsageData(
    @SerialName("package_name")
    val packageName: String,

    @SerialName("app_name")
    val appName: String = "",

    @SerialName("time_used_minutes")
    val timeUsedMinutes: Int,
)

@Serializable
data class SupabaseNotificationData(
    @SerialName("app_name")
    val appName: String,

    @SerialName("title")
    val title: String = "",

    @SerialName("text")
    val text: String = "",

    @SerialName("timestamp")
    val timestamp: Long,
)

@Serializable
data class SupabaseHealthData(
    @SerialName("step_count")
    val stepCount: Int = 0,

    @SerialName("heart_rate")
    val heartRate: Int = 0,

    @SerialName("sleep_minutes")
    val sleepMinutes: Int = 0,
)
