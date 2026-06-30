package me.rerere.rikkahub.ui.pages.setting

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Switch
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.Database02
import me.rerere.hugeicons.stroke.Health
import me.rerere.hugeicons.stroke.MapPinpoint01
import me.rerere.rikkahub.R
import me.rerere.rikkahub.ui.components.nav.BackButton
import me.rerere.rikkahub.ui.theme.CustomColors
import me.rerere.rikkahub.utils.plus

@Composable
fun SettingIntegrationPage() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("rikkahub_integrations", Context.MODE_PRIVATE) }

    var amapKey by remember { mutableStateOf(prefs.getString("amap_api_key", "") ?: "") }
    var healthDbPath by remember { mutableStateOf(prefs.getString("health_db_path", "/sdcard/Gadgetbridge/gadgetbridge.db") ?: "/sdcard/Gadgetbridge/gadgetbridge.db") }
    var supabaseUrl by remember { mutableStateOf(prefs.getString("supabase_url", "") ?: "") }
    var supabaseKey by remember { mutableStateOf(prefs.getString("supabase_key", "") ?: "") }
    var supabaseEnabled by remember { mutableStateOf(prefs.getBoolean("supabase_enabled", true)) }
    var healthEnabled by remember { mutableStateOf(prefs.getBoolean("health_enabled", true)) }

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(R.string.setting_integration_title)) },
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
            // Amap API Key
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                HugeIcons.MapPinpoint01, null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(R.string.setting_integration_amap),
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        Text(
                            text = stringResource(R.string.setting_integration_amap_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        OutlinedTextField(
                            value = amapKey,
                            onValueChange = { amapKey = it },
                            label = { Text(stringResource(R.string.setting_integration_amap_key_label)) },
                            placeholder = { Text("例如: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        TextButton(
                            onClick = {
                                prefs.edit().putString("amap_api_key", amapKey).apply()
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(stringResource(R.string.setting_integration_save))
                        }
                    }
                }
            }

            // Health Data
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                HugeIcons.Health, null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(R.string.setting_integration_health),
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(start = 8.dp).weight(1f)
                            )
                            Switch(
                                checked = healthEnabled,
                                onCheckedChange = {
                                    healthEnabled = it
                                    prefs.edit().putBoolean("health_enabled", it).apply()
                                }
                            )
                        }
                        Text(
                            text = stringResource(R.string.setting_integration_health_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        OutlinedTextField(
                            value = healthDbPath,
                            onValueChange = { healthDbPath = it },
                            enabled = healthEnabled,
                            label = { Text(stringResource(R.string.setting_integration_health_path_label)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        TextButton(
                            onClick = {
                                prefs.edit().putString("health_db_path", healthDbPath).apply()
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(stringResource(R.string.setting_integration_save))
                        }
                    }
                }
            }

            // Supabase
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                HugeIcons.Database02, null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(R.string.setting_integration_supabase),
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(start = 8.dp).weight(1f)
                            )
                            Switch(
                                checked = supabaseEnabled,
                                onCheckedChange = {
                                    supabaseEnabled = it
                                    prefs.edit().putBoolean("supabase_enabled", it).apply()
                                }
                            )
                        }
                        Text(
                            text = stringResource(R.string.setting_integration_supabase_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        OutlinedTextField(
                            value = supabaseUrl,
                            onValueChange = { supabaseUrl = it },
                            enabled = supabaseEnabled,
                            label = { Text(stringResource(R.string.setting_integration_supabase_url_label)) },
                            placeholder = { Text("https://xxxxxxxxxxxx.supabase.co") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
                        )
                        OutlinedTextField(
                            value = supabaseKey,
                            onValueChange = { supabaseKey = it },
                            enabled = supabaseEnabled,
                            label = { Text(stringResource(R.string.setting_integration_supabase_key_label)) },
                            placeholder = { Text("anon key") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        TextButton(
                            onClick = {
                                prefs.edit()
                                    .putString("supabase_url", supabaseUrl)
                                    .putString("supabase_key", supabaseKey)
                                    .apply()
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(stringResource(R.string.setting_integration_save))
                        }
                    }
                }
            }
        }
    }
}
