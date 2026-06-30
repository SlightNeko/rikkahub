package me.rerere.rikkahub.plugin.data

import android.content.Context
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class PluginDataStore(private val context: Context) {
    private val prefs = context.getSharedPreferences("plugin_data_store", Context.MODE_PRIVATE)

    fun isEnabled(pluginId: String): Boolean = prefs.getBoolean("${pluginId}_enabled", true)

    fun setEnabled(pluginId: String, enabled: Boolean) {
        prefs.edit().putBoolean("${pluginId}_enabled", enabled).apply()
    }

    fun getConfig(pluginId: String): Map<String, String> {
        val json = prefs.getString("${pluginId}_config", null) ?: return emptyMap()
        return try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun setConfig(pluginId: String, config: Map<String, String>) {
        prefs.edit().putString("${pluginId}_config", Json.encodeToString(config)).apply()
    }
}
