package me.rerere.rikkahub.plugin.di

import me.rerere.rikkahub.data.service.MemoryBankService
import me.rerere.rikkahub.data.service.SupabaseService
import me.rerere.rikkahub.plugin.data.PluginDataStore
import me.rerere.rikkahub.plugin.loader.PluginLoader
import me.rerere.rikkahub.plugin.loader.PluginSandbox
import me.rerere.rikkahub.plugin.manager.PluginManager
import me.rerere.rikkahub.plugin.repository.PluginRepository
import me.rerere.rikkahub.plugin.scanner.PluginScanner
import org.koin.dsl.module

val pluginModule = module {
    single { PluginDataStore(get()) }
    single { PluginScanner(get()) }
    single { PluginRepository(get()) }
    single { PluginSandbox(get()) }
    single { PluginLoader(get(), get(), getOrNull<MemoryBankService>(), getOrNull<SupabaseService>()) }
    single { PluginManager(get(), get(), get(), get(), get()) }
}
