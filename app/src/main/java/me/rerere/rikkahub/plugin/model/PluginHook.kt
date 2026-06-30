package me.rerere.rikkahub.plugin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PluginHook(
    val id: String,
    val name: String = "",
    val description: String = "",
    val config: PluginHookConfig? = null,
)
