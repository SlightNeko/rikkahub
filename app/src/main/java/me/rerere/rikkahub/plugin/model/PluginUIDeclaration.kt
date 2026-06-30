package me.rerere.rikkahub.plugin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PluginUIDeclaration(
    val pages: List<PluginWebViewPageConfig> = emptyList(),
    val components: List<PluginUIComponent> = emptyList(),
    val actions: List<PluginUIAction> = emptyList(),
    val queries: List<PluginUIQuery> = emptyList(),
    @SerialName("confirm_dialogs")
    val confirmDialogs: List<PluginUIConfirmDialog> = emptyList(),
)
