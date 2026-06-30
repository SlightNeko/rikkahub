package me.rerere.rikkahub.plugin.model

import java.io.File

data class PluginInfo(
    val manifest: PluginManifest,
    val directory: File,
    val isEnabled: Boolean = true,
    val config: Map<String, String> = emptyMap(),
    val loadError: String? = null,
)
