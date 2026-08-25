package org.rookieand.autoplant.service

import net.momirealms.customcrops.api.BukkitCustomCropsAPI
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin
import net.momirealms.customcrops.api.core.block.CropBlock
import net.momirealms.customcrops.api.event.CropBreakEvent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.rookieand.autoplant.configuration.PluginConfiguration

class CustomCropsService(
    private val plugin: JavaPlugin,
    private val configuration: PluginConfiguration
) {
    private val itemManager get() = BukkitCustomCropsPlugin.getInstance().itemManager

    fun tryReplant(player: Player, event: CropBreakEvent): Boolean {
        val config = event.cropConfig()

        val state = event.blockState()
        val cropBlock = state.type() as? CropBlock ?: return false
        if (cropBlock.point(state) < config.maxPoints()) return false

        val seedIds = config.seeds()
        val seedItem = player.inventory.contents.firstOrNull {
            it != null && !it.type.isAir && itemManager.id(it) in seedIds
        } ?: return false

        seedItem.amount--

        scheduleReplant(event.location(), config.id())
        return true
    }

    private fun scheduleReplant(location: Location, cropId: String) {
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            BukkitCustomCropsAPI.get().placeCrop(location, cropId, 0)
        }, configuration.replantDelayTicks)
    }
}
