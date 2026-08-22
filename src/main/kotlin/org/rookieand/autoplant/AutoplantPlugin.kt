package org.rookieand.autoplant

import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.rookieand.autoplant.api.AutoplantApi
import org.rookieand.autoplant.command.AutoplantCommand
import org.rookieand.autoplant.database.MongoConnectionException
import org.rookieand.autoplant.database.PlayerCountRepository
import org.rookieand.autoplant.listener.BlockChangesListener
import org.rookieand.autoplant.listener.PlayerConnectionListener
import org.rookieand.autoplant.scheduler.PlayerCountSyncScheduler
import org.rookieand.autoplant.storable.PlayerCountStorable

class AutoplantPlugin : JavaPlugin() {
    companion object {
        var instance: KoinApplication? = null
            private set
    }

    override fun onEnable() {
        saveDefaultConfig()
        val koin = initializeModule(this).also { instance = it }.koin

        try {
            koin.get<PlayerCountRepository>().connect()
        } catch (e: MongoConnectionException) {
            logger.severe(e.message)
            server.pluginManager.disablePlugin(this)
            return
        }

        server.pluginManager.registerEvents(koin.get<BlockChangesListener>(), this)
        server.pluginManager.registerEvents(koin.get<PlayerConnectionListener>(), this)

        koin.get<AutoplantCommand>().register()

        server.servicesManager.register(
            AutoplantApi::class.java,
            koin.get<AutoplantApi>(),
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
    modules(autoplantModule(plugin))
}
