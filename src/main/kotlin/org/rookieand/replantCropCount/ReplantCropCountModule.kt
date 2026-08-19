package org.rookieand.replantCropCount

import org.bukkit.plugin.java.JavaPlugin
import org.koin.dsl.module
import org.rookieand.replantCropCount.api.ReplantCropCountApi
import org.rookieand.replantCropCount.api.ReplantCropCountApiProvider
import org.rookieand.replantCropCount.command.ReplantCropCountCommand
import org.rookieand.replantCropCount.configuration.PluginConfiguration
import org.rookieand.replantCropCount.database.PlayerCountRepository
import org.rookieand.replantCropCount.listener.BlockChangesListener
import org.rookieand.replantCropCount.listener.PlayerConnectionListener
import org.rookieand.replantCropCount.registry.PlayerCountRegistry
import org.rookieand.replantCropCount.scheduler.PlayerCountSyncScheduler
import org.rookieand.replantCropCount.service.CropService
import org.rookieand.replantCropCount.service.PlayerCountService
import org.rookieand.replantCropCount.storable.PlayerCountStorable

fun replantCropCountModule(plugin: JavaPlugin) = module {
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

    single<ReplantCropCountApi> { ReplantCropCountApiProvider(get()) }
    single { ReplantCropCountCommand(get(), get()) }
}
