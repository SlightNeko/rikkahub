package me.rerere.rikkahub.plugin.model

import kotlinx.serialization.Serializable

@Serializable
data class PluginToolDefinition(
    val name: String,
    val description: String = "",
    val parameters: List<PluginToolParameter> = emptyList(),
)

@Serializable
data class PluginToolParameter(
    val name: String,
    val type: String = "string",  // string, number, boolean
    val description: String = "",
    val required: Boolean = false,
)
