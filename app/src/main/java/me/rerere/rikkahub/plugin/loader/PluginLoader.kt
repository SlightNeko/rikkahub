package me.rerere.rikkahub.plugin.loader

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import me.rerere.rikkahub.data.service.MemoryBankService
import me.rerere.rikkahub.data.service.SupabaseService
import me.rerere.rikkahub.plugin.model.PluginInfo
import okhttp3.OkHttpClient
import java.util.concurrent.Executors

/**
 * Manages loading and unloading of plugins.
 * Receives optional services via constructor; they may be null
 * if not yet available.
 */
class PluginLoader(
    private val context: Context,
    private val okHttpClient: OkHttpClient,
    private val memoryBankService: MemoryBankService? = null,
    private val supabaseService: SupabaseService? = null,
) {
    private val loadedPlugins = mutableMapOf<String, LoadedPlugin>()
    private val executor = Executors.newSingleThreadExecutor { r ->
        Thread(r, "plugin-dispatcher")
    }

    fun loadPlugin(info: PluginInfo): LoadedPlugin {
        val sandbox = PluginSandbox(context)
        val loaded = LoadedPlugin(info = info, sandbox = sandbox)
        loadedPlugins[info.manifest.id] = loaded
        return loaded
    }

    fun unloadPlugin(pluginId: String) {
        loadedPlugins.remove(pluginId)
    }

    fun getPluginsWithHook(hookName: String): List<LoadedPlugin> {
        return loadedPlugins.values.filter { it.info.manifest.hooks.contains(hookName) }
    }

    suspend fun callEvent(hookName: String, eventData: JsonElement) {
        val plugins = getPluginsWithHook(hookName)
        // For each plugin, the JS handler would be invoked
        // For now, just log
        withContext(Dispatchers.IO) {
            // Stub — real execution via WebView will come in step 3
        }
    }

    suspend fun callTool(pluginId: String, toolName: String, params: JsonElement): JsonElement {
        val plugin = loadedPlugins[pluginId] ?: throw Exception("Plugin not loaded: $pluginId")
        // Stub — real implementation will invoke JS in the sandbox
        return buildJsonObject { put("result", "ok") }
    }
}
