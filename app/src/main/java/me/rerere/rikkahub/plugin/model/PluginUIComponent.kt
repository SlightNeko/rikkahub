package me.rerere.rikkahub.plugin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PluginUIComponent(
    val id: String,
    val type: String = "",        // e.g. "card", "button", "list"
    val page: String = "",        // which page this component belongs to
    val props: Map<String, String> = emptyMap(),
    @SerialName("slot_index")
    val slotIndex: Int = 0,
)
