package me.rerere.rikkahub.plugin.webview

import me.rerere.rikkahub.plugin.data.PluginDataStore
import me.rerere.rikkahub.plugin.loader.PluginLoader
import me.rerere.rikkahub.plugin.model.PluginInfo

/**
 * Bridge between the WebView-based plugin JS runtime and the Android host.
 * Provides the window.NekoHub API surface for plugins.
 */
class PluginWebViewClient(
    private val pluginInfo: PluginInfo,
    private val dataStore: PluginDataStore,
    private val pluginLoader: PluginLoader,
) {
    /**
     * Called by JS: window.NekoHub.callAI(prompt)
     * Stub — full AI integration comes with PluginManager later.
     */
    suspend fun callAI(prompt: String): String {
        return """{"success":false,"error":"AI not configured"}"""
    }

    /**
     * Called by JS: window.NekoHub.getConfig(key)
     */
    fun getConfig(key: String): String? {
        return dataStore.getConfig(pluginInfo.manifest.id)[key]
    }

    /**
     * Called by JS: window.NekoHub.setConfig(key, value)
     */
    fun setConfig(key: String, value: String) {
        val config = dataStore.getConfig(pluginInfo.manifest.id).toMutableMap()
        config[key] = value
        dataStore.setConfig(pluginInfo.manifest.id, config)
    }

    /**
     * Called by JS: window.NekoHub.emitEvent(name, data)
     * Stub for now — event propagation will be wired up later.
     */
    fun emitEvent(name: String, data: String) {
        // Stub for now
    }
}
