package me.rerere.rikkahub.plugin.manager

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.rerere.rikkahub.plugin.data.PluginDataStore
import me.rerere.rikkahub.plugin.loader.PluginLoader
import me.rerere.rikkahub.plugin.model.PluginInfo
import me.rerere.rikkahub.plugin.repository.PluginRepository
import me.rerere.rikkahub.plugin.scanner.PluginScanner

class PluginManager(
    private val context: Context,
    private val scanner: PluginScanner,
    private val loader: PluginLoader,
    private val dataStore: PluginDataStore,
    private val repository: PluginRepository,
) {
    private val _plugins = MutableStateFlow<List<PluginInfo>>(emptyList())
    val plugins: StateFlow<List<PluginInfo>> = _plugins.asStateFlow()

    fun refreshPlugins() {
        val scanned = scanner.scan()
        _plugins.value = scanned.map { info ->
            info.copy(
                isEnabled = dataStore.isEnabled(info.manifest.id),
                config = dataStore.getConfig(info.manifest.id),
            )
        }
    }

    fun loadPlugin(pluginId: String) {
        val info = _plugins.value.find { it.manifest.id == pluginId } ?: return
        loader.loadPlugin(info)
    }

    fun unloadPlugin(pluginId: String) {
        loader.unloadPlugin(pluginId)
    }

    fun togglePlugin(pluginId: String, enabled: Boolean) {
        dataStore.setEnabled(pluginId, enabled)
        if (enabled) loadPlugin(pluginId) else unloadPlugin(pluginId)
        refreshPlugins()
    }

    fun updatePluginConfig(pluginId: String, config: Map<String, String>) {
        dataStore.setConfig(pluginId, config)
        refreshPlugins()
    }

    suspend fun deletePlugin(pluginId: String) {
        val info = _plugins.value.find { it.manifest.id == pluginId } ?: return
        unloadPlugin(pluginId)
        info.directory.deleteRecursively()
        refreshPlugins()
    }
}
