package me.rerere.rikkahub.plugin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PluginHookConfig(
    @SerialName("cron_expression")
    val cronExpression: String = "",
    @SerialName("trigger_event")
    val triggerEvent: String = "",
    val params: Map<String, String> = emptyMap(),
)
