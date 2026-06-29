package me.rerere.rikkahub.ui.pages.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.rerere.rikkahub.R
import me.rerere.rikkahub.ui.components.nav.BackButton
import me.rerere.rikkahub.ui.components.ui.CardGroup
import me.rerere.rikkahub.ui.theme.CustomColors
import me.rerere.rikkahub.utils.plus
import kotlin.math.roundToInt
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingAutoCompressPage(vm: SettingVM = koinViewModel()) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val settings by vm.settings.collectAsStateWithLifecycle()

    var triggerTurns by remember { mutableFloatStateOf(settings.autoCompressTriggerTurns.toFloat()) }
    var triggerTokens by remember { mutableFloatStateOf(settings.autoCompressTriggerTokens.toFloat()) }
    var targetTokens by remember { mutableFloatStateOf(settings.autoCompressTargetTokens.toFloat()) }
    var keepRecent by remember { mutableFloatStateOf(settings.autoCompressKeepRecent.toFloat()) }

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(R.string.setting_auto_compress)) },
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
            // Description
            item {
                CardGroup(
                    modifier = Modifier.padding(horizontal = 8.dp),
                ) {
                    item {
                        Text(
                            text = stringResource(R.string.setting_auto_compress_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            item {
                CardGroup(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    title = { Text(stringResource(R.string.setting_auto_compress)) }
                ) {
                    // Enable/Disable toggle
                    item(
                        headlineContent = {
                            Text(stringResource(R.string.setting_auto_compress_enabled))
                        },
                        trailingContent = {
                            Switch(
                                checked = settings.autoCompressEnabled,
                                onCheckedChange = { enabled ->
                                    vm.updateSettings(settings.copy(autoCompressEnabled = enabled))
                                }
                            )
                        }
                    )

                    // Trigger turns slider
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = stringResource(
                                    R.string.setting_auto_compress_trigger_turns,
                                    settings.autoCompressTriggerTurns
                                ),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Slider(
                                value = triggerTurns,
                                onValueChange = { triggerTurns = it },
                                valueRange = 5f..100f,
                                steps = 18, // (100-5)/5 - 1 ≈ 18
                                onValueChangeFinished = {
                                    vm.updateSettings(
                                        settings.copy(autoCompressTriggerTurns = triggerTurns.roundToInt())
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "${triggerTurns.roundToInt()} ${stringResource(R.string.setting_auto_compress_turns_unit)}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Trigger tokens slider
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = stringResource(
                                    R.string.setting_auto_compress_trigger_tokens,
                                    settings.autoCompressTriggerTokens
                                ),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Slider(
                                value = triggerTokens,
                                onValueChange = { triggerTokens = it },
                                valueRange = 1000f..32000f,
                                steps = 30, // (32000-1000)/1000 - 1 ≈ 30
                                onValueChangeFinished = {
                                    vm.updateSettings(
                                        settings.copy(autoCompressTriggerTokens = triggerTokens.roundToInt())
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "${triggerTokens.roundToInt()} tokens",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Target tokens slider
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = stringResource(
                                    R.string.setting_auto_compress_target_tokens,
                                    settings.autoCompressTargetTokens
                                ),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Slider(
                                value = targetTokens,
                                onValueChange = { targetTokens = it },
                                valueRange = 500f..16000f,
                                steps = 30, // (16000-500)/500 - 1 ≈ 30
                                onValueChangeFinished = {
                                    vm.updateSettings(
                                        settings.copy(autoCompressTargetTokens = targetTokens.roundToInt())
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "${targetTokens.roundToInt()} tokens",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Keep recent messages slider
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = stringResource(
                                    R.string.setting_auto_compress_keep_recent,
                                    settings.autoCompressKeepRecent
                                ),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Slider(
                                value = keepRecent,
                                onValueChange = { keepRecent = it },
                                valueRange = 1f..20f,
                                steps = 18, // (20-1)/1 - 1 = 18
                                onValueChangeFinished = {
                                    vm.updateSettings(
                                        settings.copy(autoCompressKeepRecent = keepRecent.roundToInt())
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "${keepRecent.roundToInt()}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Additional prompt text field
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            OutlinedTextField(
                                value = settings.autoCompressAdditionalPrompt,
                                onValueChange = { newPrompt ->
                                    vm.updateSettings(
                                        settings.copy(autoCompressAdditionalPrompt = newPrompt)
                                    )
                                },
                                label = {
                                    Text(stringResource(R.string.setting_auto_compress_additional_prompt))
                                },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 4,
                            )
                        }
                    }
                }
            }
        }
    }
}
