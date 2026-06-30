package me.rerere.rikkahub.plugin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PluginUIConfirmDialog(
    val id: String,
    val title: String = "",
    val message: String = "",
    @SerialName("confirm_label")
    val confirmLabel: String = "Confirm",
    @SerialName("cancel_label")
    val cancelLabel: String = "Cancel",
)
