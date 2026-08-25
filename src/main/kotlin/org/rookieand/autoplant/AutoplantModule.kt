package org.rookieand.autoplant

import org.bukkit.plugin.java.JavaPlugin
import org.koin.dsl.module
import org.rookieand.autoplant.api.AutoplantApi
import org.rookieand.autoplant.api.AutoplantApiProvider
import org.rookieand.autoplant.command.AutoplantCommand
import org.rookieand.autoplant.configuration.PluginConfiguration
import org.rookieand.autoplant.database.PlayerCountRepository
import org.rookieand.autoplant.listener.BlockChangesListener
import org.rookieand.autoplant.listener.PlayerConnectionListener
import org.rookieand.autoplant.registry.PlayerCountRegistry
import org.rookieand.autoplant.scheduler.PlayerCountSyncScheduler
import org.rookieand.autoplant.service.CropService
import org.rookieand.autoplant.service.PlayerCountService
import org.rookieand.autoplant.storable.PlayerCountStorable

fun autoplantModule(plugin: JavaPlugin) = module {
    single { plugin }

    single { PluginConfiguration(get()) }

    single { PlayerCountRegistry() }
    single { PlayerCountRepository(get()) }
    single { PlayerCountStorable(get(), get()) }
    single { PlayerCountService(get(), get()) }
    single { CropService(get(), get()) }

    single { BlockChangesListener(get(), get()) }
    single { PlayerConnectionListener(get(), get()) }

    single { PlayerCountSyncScheduler(get(), get(), get()) }

    single<AutoplantApi> { AutoplantApiProvider(get()) }
    single { AutoplantCommand(get(), get()) }
}
