package me.rerere.rikkahub.data.ai.tools.integration

import android.content.Context
import me.rerere.ai.core.Tool
import me.rerere.rikkahub.data.ai.tools.local.buildProactiveMessageTool
import me.rerere.rikkahub.data.ai.tools.local.buildGadgetbridgeTool
import me.rerere.rikkahub.data.ai.tools.local.buildSupabaseTool

/**
 * Provides integration tools (ProactiveMessage, HealthData, Supabase)
 * that are NOT part of the local tools list — they're managed through
 * the Integration settings page instead.
 */
class IntegrationTools(private val context: Context) {
    val proactiveMessage by lazy { buildProactiveMessageTool(context) }
    val healthData by lazy { buildGadgetbridgeTool(context) }
    val supabase by lazy { buildSupabaseTool(context) }

    fun getAll(): List<Tool> = listOf(proactiveMessage, healthData, supabase)
}
