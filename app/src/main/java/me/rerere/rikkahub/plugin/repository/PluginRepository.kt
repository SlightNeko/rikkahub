package me.rerere.rikkahub.plugin.repository

import android.content.Context
import kotlinx.serialization.json.Json
import me.rerere.rikkahub.plugin.model.PluginManifest
import java.io.File
import java.util.zip.ZipInputStream

class PluginRepository(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }

    fun getPluginsDir(): File = File(context.filesDir, "plugins")

    fun installPlugin(zipFile: File): Boolean {
        return try {
            val pluginsDir = getPluginsDir()
            pluginsDir.mkdirs()
            ZipInputStream(zipFile.inputStream()).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    val target = File(pluginsDir, entry.name)
                    if (entry.isDirectory) {
                        target.mkdirs()
                    } else {
                        target.parentFile?.mkdirs()
                        target.outputStream().use { zis.copyTo(it) }
                    }
                    entry = zis.nextEntry
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getPluginDir(pluginId: String): File? {
        return getPluginsDir().listFiles()?.find {
            it.isDirectory && File(it, "manifest.json").let { mf ->
                mf.exists() && try {
                    json.decodeFromString<PluginManifest>(mf.readText()).id == pluginId
                } catch (e: Exception) {
                    false
                }
            }
        }
    }
}
