package me.rerere.rikkahub.plugin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PluginManifest(
    val id: String,
    val name: String,
    val version: String,
    val author: String = "",
    val description: String = "",
    val icon: String = "",          // base64 or relative path
    val entry: String = "",         // entry HTML/JS file
    val permissions: List<String> = emptyList(),
    val tools: List<PluginToolDefinition> = emptyList(),
    val config: List<ConfigOption> = emptyList(),
    val hooks: List<String> = emptyList(),  // e.g. "daily_cron"
    val promptTemplate: String = "",
    @SerialName("custom_page")
    val customPage: String = "",
    val ui: PluginUIDeclaration? = null,
)
