package org.rookieand.autoplant.service

import org.bukkit.Bukkit
import org.bukkit.block.Block
import org.bukkit.block.data.Ageable
import org.bukkit.block.data.Directional
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.rookieand.autoplant.configuration.PluginConfiguration

class CropService(
    private val plugin: JavaPlugin,
    private val configuration: PluginConfiguration
) {
    fun tryReplant(player: Player, block: Block): Boolean {
        val cropType = block.type
        val seedType = configuration.cropSeeds[cropType] ?: return false

        val blockData = block.blockData
        if (blockData is Ageable && blockData.age != blockData.maximumAge) return false

        val seedItem = player.inventory.contents
            .firstOrNull { it != null && it.type == seedType } ?: return false

        seedItem.amount--

        val replantType = configuration.stemReplants[cropType] ?: cropType
        val facing = (blockData as? Directional)?.facing

        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            block.type = replantType
            if (facing != null) {
                val newData = block.blockData
                if (newData is Directional) {
                    newData.facing = facing
                    block.blockData = newData
                }
            }
        }, configuration.replantDelayTicks)
        return true
    }
}
