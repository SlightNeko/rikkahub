package me.rerere.rikkahub.plugin.model

import kotlinx.serialization.Serializable

@Serializable
data class ConfigOption(
    val key: String,
    val label: String = "",
    val type: String = "string",  // string, number, boolean, select
    val default: String = "",
    val options: List<String> = emptyList(),
    val description: String = "",
    val required: Boolean = false,
)
