package me.rerere.rikkahub.plugin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PluginUIAction(
    val id: String,
    val label: String = "",
    val handler: String = "",     // JS function name
    @SerialName("confirm_dialog_id")
    val confirmDialogId: String = "",
)
