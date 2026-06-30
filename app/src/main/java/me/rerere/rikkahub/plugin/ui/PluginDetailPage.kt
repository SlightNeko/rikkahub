package me.rerere.rikkahub.plugin.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.rerere.rikkahub.plugin.manager.PluginManager
import me.rerere.rikkahub.ui.components.nav.BackButton
import me.rerere.rikkahub.ui.theme.CustomColors
import me.rerere.rikkahub.utils.plus
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PluginDetailPage(pluginId: String) {
    val manager: PluginManager = koinInject()
    val plugins by manager.plugins.collectAsStateWithLifecycle()
    val plugin = plugins.find { it.manifest.id == pluginId }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(plugin?.manifest?.name ?: "Plugin") },
                navigationIcon = { BackButton() },
                scrollBehavior = scrollBehavior,
                colors = CustomColors.topBarColors
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = CustomColors.topBarColors.containerColor
    ) { innerPadding ->
        if (plugin == null) {
            Text(
                text = "Plugin not found",
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = innerPadding + PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("ID: ${plugin.manifest.id}")
                }
                item {
                    Text("Version: ${plugin.manifest.version}")
                }
                item {
                    Text("Author: ${plugin.manifest.author}")
                }
                item {
                    Text(plugin.manifest.description)
                }
                item {
                    Switch(
                        checked = plugin.isEnabled,
                        onCheckedChange = { manager.togglePlugin(plugin.manifest.id, it) }
                    )
                }
            }
        }
    }
}
