package me.rerere.rikkahub.ui.pages.setting

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.Alert01
import me.rerere.hugeicons.stroke.CheckmarkCircle02
import me.rerere.rikkahub.R
import me.rerere.rikkahub.ui.components.nav.BackButton
import me.rerere.rikkahub.ui.theme.CustomColors
import me.rerere.rikkahub.utils.plus

@Composable
fun SettingPermissionPage() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(R.string.setting_permission_title)) },
                navigationIcon = { BackButton() },
                scrollBehavior = scrollBehavior,
                colors = CustomColors.topBarColors
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = CustomColors.topBarColors.containerColor
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding + PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                PermissionGroupCard(
                    title = stringResource(R.string.setting_permission_essential),
                    items = listOf(
                        Triple(stringResource(R.string.permission_camera), stringResource(R.string.permission_camera_desc), Manifest.permission.CAMERA),
                        Triple(stringResource(R.string.permission_calendar_read), stringResource(R.string.permission_calendar_read_desc), Manifest.permission.READ_CALENDAR),
                        Triple(stringResource(R.string.permission_calendar_write), stringResource(R.string.permission_calendar_write_desc), Manifest.permission.WRITE_CALENDAR),
                        Triple(stringResource(R.string.permission_location), stringResource(R.string.permission_location_desc), Manifest.permission.ACCESS_FINE_LOCATION),
                    )
                )
            }

            item {
                PermissionGroupCard(
                    title = stringResource(R.string.setting_permission_communication),
                    items = listOf(
                        Triple(stringResource(R.string.permission_sms), stringResource(R.string.permission_sms_desc), Manifest.permission.READ_SMS),
                    )
                )
            }

            // Special permissions that require system settings
            item {
                val alarmGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val am = context.getSystemService(android.app.AlarmManager::class.java)
                    am.canScheduleExactAlarms()
                } else true

                val notifGranted = run {
                    val listeners = Settings.Secure.getString(
                        context.contentResolver, "enabled_notification_listeners"
                    )
                    listeners?.contains(context.packageName) == true
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = stringResource(R.string.setting_permission_special),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Alarm
                        PermissionRow(
                            label = stringResource(R.string.permission_alarm),
                            desc = stringResource(R.string.assistant_page_local_tools_alarm_permission_required),
                            granted = alarmGranted,
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                                }
                            }
                        )

                        // Notification Listener
                        PermissionRow(
                            label = stringResource(R.string.permission_notification_listener),
                            desc = stringResource(R.string.assistant_page_local_tools_notification_permission_required),
                            granted = notifGranted,
                            onClick = {
                                context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                            }
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = stringResource(R.string.setting_permission_tip),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionGroupCard(
    title: String,
    items: List<Triple<String, String, String>>
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            items.forEach { (label, desc, permission) ->
                val granted = ContextCompat.checkSelfPermission(context, permission) ==
                        android.content.pm.PackageManager.PERMISSION_GRANTED
                PermissionRow(
                    label = label,
                    desc = desc,
                    granted = granted,
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
private fun PermissionRow(
    label: String,
    desc: String,
    granted: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (granted) HugeIcons.CheckmarkCircle02 else HugeIcons.Alert01,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = if (granted) Color(0xFF4CAF50) else Color(0xFFFF5722)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = if (granted) stringResource(R.string.permission_status_granted)
                   else stringResource(R.string.permission_status_denied),
            color = if (granted) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
