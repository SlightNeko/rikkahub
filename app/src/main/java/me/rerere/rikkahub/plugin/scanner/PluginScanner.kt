package me.rerere.rikkahub.plugin.scanner

import android.content.Context
import kotlinx.serialization.json.Json
import me.rerere.rikkahub.plugin.model.PluginInfo
import me.rerere.rikkahub.plugin.model.PluginManifest
import java.io.File

class PluginScanner(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }

    fun scan(): List<PluginInfo> {
        val pluginsDir = File(context.filesDir, "plugins")
        if (!pluginsDir.exists()) return emptyList()

        return pluginsDir.listFiles()
            ?.filter { it.isDirectory }
            ?.mapNotNull { dir ->
                val manifestFile = File(dir, "manifest.json")
                if (manifestFile.exists()) {
                    try {
                        val manifest = json.decodeFromString<PluginManifest>(manifestFile.readText())
                        PluginInfo(manifest = manifest, directory = dir)
                    } catch (e: Exception) {
                        null
                    }
                } else null
            } ?: emptyList()
    }
}
