package org.rookieand.autoplant.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import org.rookieand.autoplant.storable.PlayerCountStorable

class PlayerConnectionListener(
    private val plugin: JavaPlugin,
    private val storable: PlayerCountStorable
) : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val uuid = event.player.uniqueId
        plugin.server.scheduler.runTaskAsynchronously(plugin, Runnable { storable.load(uuid) })
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val uuid = event.player.uniqueId
        plugin.server.scheduler.runTaskAsynchronously(plugin, Runnable { storable.unload(uuid) })
    }
}
