package me.rerere.rikkahub.plugin.loader

import me.rerere.rikkahub.plugin.model.PluginInfo

data class LoadedPlugin(
    val info: PluginInfo,
    val sandbox: PluginSandbox,
)
