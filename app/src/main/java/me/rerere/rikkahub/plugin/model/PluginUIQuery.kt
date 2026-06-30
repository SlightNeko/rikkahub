package me.rerere.rikkahub.plugin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PluginUIQuery(
    val id: String,
    val description: String = "",
    val handler: String = "",     // JS function name
    @SerialName("refresh_interval_seconds")
    val refreshIntervalSeconds: Int = 0,
)
