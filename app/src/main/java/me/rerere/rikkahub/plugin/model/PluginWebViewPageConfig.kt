package me.rerere.rikkahub.plugin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PluginWebViewPageConfig(
    val id: String,
    val title: String = "",
    val url: String = "",
    @SerialName("show_in_nav")
    val showInNav: Boolean = false,
    @SerialName("nav_icon")
    val navIcon: String = "",
)
