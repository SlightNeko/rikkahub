package me.rerere.rikkahub.data.ai.tools.local

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class LocalToolOption {
    @Serializable
    @SerialName("javascript_engine")
    data object JavascriptEngine : LocalToolOption()

    @Serializable
    @SerialName("time_info")
    data object TimeInfo : LocalToolOption()

    @Serializable
    @SerialName("clipboard")
    data object Clipboard : LocalToolOption()

    @Serializable
    @SerialName("tts")
    data object Tts : LocalToolOption()

    @Serializable
    @SerialName("ask_user")
    data object AskUser : LocalToolOption()

    @Serializable
    @SerialName("screen_time")
    data object ScreenTime : LocalToolOption()

    @Serializable
    @SerialName("calendar")
    data object Calendar : LocalToolOption()

    @Serializable
    @SerialName("location")
    data object Location : LocalToolOption()

    @Serializable
    @SerialName("notification")
    data object Notification : LocalToolOption()

    @Serializable
    @SerialName("app_usage_trajectory")
    data object AppUsageTrajectory : LocalToolOption()

    @Serializable
    @SerialName("nearby_poi")
    data object NearbyPoi : LocalToolOption()

    @Serializable
    @SerialName("battery")
    data object Battery : LocalToolOption()

    @Serializable
    @SerialName("sms")
    data object Sms : LocalToolOption()

    @Serializable
    @SerialName("music_control")
    data object MusicControl : LocalToolOption()

    @Serializable
    @SerialName("camera")
    data object Camera : LocalToolOption()

    @Serializable
    @SerialName("alarm")
    data object Alarm : LocalToolOption()

    @Serializable
    @SerialName("screen_events")
    data object ScreenEvents : LocalToolOption()
}

fun LocalToolOption.toSerialName(): String = when (this) {
    LocalToolOption.JavascriptEngine -> "javascript_engine"
    LocalToolOption.TimeInfo -> "time_info"
    LocalToolOption.Clipboard -> "clipboard"
    LocalToolOption.Tts -> "tts"
    LocalToolOption.AskUser -> "ask_user"
    LocalToolOption.ScreenTime -> "screen_time"
    LocalToolOption.Calendar -> "calendar"
    LocalToolOption.Location -> "location"
    LocalToolOption.Notification -> "notification"
    LocalToolOption.AppUsageTrajectory -> "app_usage_trajectory"
    LocalToolOption.NearbyPoi -> "nearby_poi"
    LocalToolOption.Battery -> "battery"
    LocalToolOption.Sms -> "sms"
    LocalToolOption.MusicControl -> "music_control"
    LocalToolOption.Camera -> "camera"
    LocalToolOption.Alarm -> "alarm"
    LocalToolOption.ScreenEvents -> "screen_events"
}
