package me.rerere.rikkahub.data.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

/**
 * Receives boot-completed and explicit sync-action broadcasts and starts
 * [SupabaseSyncService] as a foreground service to begin periodic data uploads.
 */
class SupabaseSyncReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == SupabaseSyncService.ACTION_SYNC
        ) {
            val serviceIntent = Intent(context, SupabaseSyncService::class.java)
            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }
}
