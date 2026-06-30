package me.rerere.rikkahub.plugin.loader

import android.content.Context

/**
 * Lightweight sandbox for JS execution.
 * Currently a stub using Android's built-in WebView.
 * Real WebView-based execution will be added in step 3.
 */
class PluginSandbox(private val context: Context) {

    fun escapeJson(s: String): String {
        return s.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }

    /**
     * Evaluate JavaScript in a sandboxed WebView.
     * Stub — will be implemented with a hidden WebView in step 3.
     */
    fun evaluateScript(script: String): String {
        return ""
    }
}
