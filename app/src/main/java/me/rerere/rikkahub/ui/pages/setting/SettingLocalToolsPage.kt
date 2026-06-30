package me.rerere.rikkahub.ui.pages.setting

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.AlarmClock
import me.rerere.hugeicons.stroke.BatteryFull
import me.rerere.hugeicons.stroke.Calendar01
import me.rerere.hugeicons.stroke.Camera01
import me.rerere.hugeicons.stroke.Codesandbox
import me.rerere.hugeicons.stroke.Copy01
import me.rerere.hugeicons.stroke.Database02
import me.rerere.hugeicons.stroke.Favourite
import me.rerere.hugeicons.stroke.HeadphoneMute
import me.rerere.hugeicons.stroke.Location01
import me.rerere.hugeicons.stroke.MarketAnalysis
import me.rerere.hugeicons.stroke.Message01
import me.rerere.hugeicons.stroke.MusicNote01
import me.rerere.hugeicons.stroke.Notification01
import me.rerere.hugeicons.stroke.Time02
import me.rerere.hugeicons.stroke.WavingHand01
import me.rerere.rikkahub.R
import me.rerere.rikkahub.data.ai.tools.local.LocalToolOption
import me.rerere.rikkahub.data.ai.tools.local.toSerialName
import me.rerere.rikkahub.ui.components.nav.BackButton
import me.rerere.rikkahub.ui.components.ui.CardGroup
import me.rerere.rikkahub.ui.theme.CustomColors
import me.rerere.rikkahub.utils.plus

val ALL_LOCAL_TOOLS = listOf(
    LocalToolOption.JavascriptEngine to R.string.assistant_page_local_tools_javascript_engine_title,
    LocalToolOption.TimeInfo to R.string.assistant_page_local_tools_time_info_title,
    LocalToolOption.Clipboard to R.string.assistant_page_local_tools_clipboard_title,
    LocalToolOption.Tts to R.string.assistant_page_local_tools_tts_title,
    LocalToolOption.AskUser to R.string.assistant_page_local_tools_ask_user_title,
    LocalToolOption.ScreenTime to R.string.assistant_page_local_tools_screen_time_title,
    LocalToolOption.Calendar to R.string.assistant_page_local_tools_calendar_title,
    LocalToolOption.Location to R.string.assistant_page_local_tools_location_title,
    LocalToolOption.Notification to R.string.assistant_page_local_tools_notification_title,
    LocalToolOption.AppUsageTrajectory to R.string.assistant_page_local_tools_app_usage_trajectory_title,
    LocalToolOption.NearbyPoi to R.string.assistant_page_local_tools_nearby_poi_title,
    LocalToolOption.Battery to R.string.assistant_page_local_tools_battery_title,
    LocalToolOption.Sms to R.string.assistant_page_local_tools_sms_title,
    LocalToolOption.MusicControl to R.string.assistant_page_local_tools_music_control_title,
    LocalToolOption.Camera to R.string.assistant_page_local_tools_camera_title,
    LocalToolOption.Alarm to R.string.assistant_page_local_tools_alarm_title,
    LocalToolOption.ScreenEvents to R.string.assistant_page_local_tools_screen_events_title,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingLocalToolsPage() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("rikkahub_local_tools", Context.MODE_PRIVATE)
    }

    val switchStates = remember {
        val map = mutableStateMapOf<String, Boolean>()
        ALL_LOCAL_TOOLS.forEach { (option, _) ->
            val key = option.toSerialName()
            map[key] = prefs.getBoolean(key, true)
        }
        map
    }

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(R.string.setting_local_tools_title)) },
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
                Text(
                    text = stringResource(R.string.setting_local_tools_desc),
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // "一键启用所有系统工具" button
            item {
                Button(
                    onClick = {
                        ALL_LOCAL_TOOLS.forEach { (option, _) ->
                            val key = option.toSerialName()
                            switchStates[key] = true
                            prefs.edit().putBoolean(key, true).apply()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Text(stringResource(R.string.setting_local_tools_enable_all))
                }
            }

            item {
                CardGroup(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    title = { Text(stringResource(R.string.setting_local_tools_all_tools)) }
                ) {
                    ALL_LOCAL_TOOLS.forEach { (option, titleRes) ->
                        val icon = toolIcon(option)
                        val title = context.getString(titleRes)
                        val desc = toolDesc(option, context)
                        val toolKey = option.toSerialName()
                        val enabled = switchStates[toolKey] ?: true

                        item(
                            leadingContent = { Icon(icon, null, modifier = Modifier.size(24.dp)) },
                            headlineContent = { Text(title) },
                            supportingContent = { Text(desc) },
                            trailingContent = {
                                Switch(
                                    checked = enabled,
                                    onCheckedChange = { newValue ->
                                        switchStates[toolKey] = newValue
                                        prefs.edit().putBoolean(toolKey, newValue).apply()
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun toolIcon(option: LocalToolOption) = when (option) {
    LocalToolOption.JavascriptEngine -> HugeIcons.Codesandbox
    LocalToolOption.TimeInfo -> HugeIcons.Time02
    LocalToolOption.Clipboard -> HugeIcons.Copy01
    LocalToolOption.Tts -> HugeIcons.HeadphoneMute
    LocalToolOption.AskUser -> HugeIcons.WavingHand01
    LocalToolOption.ScreenTime -> HugeIcons.Favourite
    LocalToolOption.Calendar -> HugeIcons.Calendar01
    LocalToolOption.Location -> HugeIcons.Location01
    LocalToolOption.Notification -> HugeIcons.Notification01
    LocalToolOption.AppUsageTrajectory -> HugeIcons.MarketAnalysis
    LocalToolOption.NearbyPoi -> HugeIcons.Location01
    LocalToolOption.Battery -> HugeIcons.BatteryFull
    LocalToolOption.Sms -> HugeIcons.Message01
    LocalToolOption.MusicControl -> HugeIcons.MusicNote01
    LocalToolOption.Camera -> HugeIcons.Camera01
    LocalToolOption.Alarm -> HugeIcons.AlarmClock
    LocalToolOption.ScreenEvents -> HugeIcons.Favourite
}

private fun toolDesc(option: LocalToolOption, context: Context): String {
    val id: Int? = when (option) {
        LocalToolOption.JavascriptEngine -> R.string.assistant_page_local_tools_javascript_engine_desc
        LocalToolOption.TimeInfo -> R.string.assistant_page_local_tools_time_info_desc
        LocalToolOption.Clipboard -> R.string.assistant_page_local_tools_clipboard_desc
        LocalToolOption.Tts -> R.string.assistant_page_local_tools_tts_desc
        LocalToolOption.AskUser -> R.string.assistant_page_local_tools_ask_user_desc
        LocalToolOption.ScreenTime -> R.string.assistant_page_local_tools_screen_time_desc
        LocalToolOption.Calendar -> R.string.assistant_page_local_tools_calendar_desc
        LocalToolOption.Location -> R.string.assistant_page_local_tools_location_desc
        LocalToolOption.Notification -> R.string.assistant_page_local_tools_notification_desc
        LocalToolOption.AppUsageTrajectory -> R.string.assistant_page_local_tools_app_usage_trajectory_desc
        LocalToolOption.NearbyPoi -> R.string.assistant_page_local_tools_nearby_poi_desc
        LocalToolOption.Battery -> R.string.assistant_page_local_tools_battery_desc
        LocalToolOption.Sms -> R.string.assistant_page_local_tools_sms_desc
        LocalToolOption.MusicControl -> R.string.assistant_page_local_tools_music_control_desc
        LocalToolOption.Camera -> R.string.assistant_page_local_tools_camera_desc
        LocalToolOption.Alarm -> R.string.assistant_page_local_tools_alarm_desc
        LocalToolOption.ScreenEvents -> R.string.assistant_page_local_tools_screen_events_desc
    }
    return if (id != null) context.getString(id) else ""
}
