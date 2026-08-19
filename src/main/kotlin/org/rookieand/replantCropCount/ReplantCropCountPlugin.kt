package org.rookieand.replantCropCount

import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.rookieand.replantCropCount.api.ReplantCropCountApi
import org.rookieand.replantCropCount.command.ReplantCropCountCommand
import org.rookieand.replantCropCount.database.PlayerCountRepository
import org.rookieand.replantCropCount.listener.BlockChangesListener
import org.rookieand.replantCropCount.listener.PlayerConnectionListener
import org.rookieand.replantCropCount.scheduler.PlayerCountSyncScheduler
import org.rookieand.replantCropCount.storable.PlayerCountStorable

class ReplantCropCountPlugin : JavaPlugin() {
    companion object {
        var instance: KoinApplication? = null
            private set
    }

    override fun onEnable() {
        saveDefaultConfig()
        val koin = initializeModule(this).also { instance = it }.koin

        koin.get<PlayerCountRepository>().connect()

        server.pluginManager.registerEvents(koin.get<BlockChangesListener>(), this)
        server.pluginManager.registerEvents(koin.get<PlayerConnectionListener>(), this)

        koin.get<ReplantCropCountCommand>().register()

        server.servicesManager.register(
            ReplantCropCountApi::class.java,
            koin.get<ReplantCropCountApi>(),
            this,
            ServicePriority.Normal
        )

        koin.get<PlayerCountSyncScheduler>().start()

        val storable = koin.get<PlayerCountStorable>()
        server.onlinePlayers.forEach { player ->
            server.scheduler.runTaskAsynchronously(this, Runnable { storable.load(player.uniqueId) })
        }
    }

    override fun onDisable() {
        val koin = instance?.koin ?: return
        koin.get<PlayerCountSyncScheduler>().stop()
        koin.get<PlayerCountStorable>().save()
        koin.get<PlayerCountRepository>().close()
        stopKoin()
        instance = null
    }
}

fun initializeModule(plugin: JavaPlugin) = startKoin {
    modules(replantCropCountModule(plugin))
}
