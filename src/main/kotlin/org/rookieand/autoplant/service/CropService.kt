package org.rookieand.autoplant.service

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.block.data.BlockData
import org.bukkit.plugin.java.JavaPlugin
import org.rookieand.autoplant.configuration.PluginConfiguration

class CropService(
    private val plugin: JavaPlugin,
    private val configuration: PluginConfiguration
) {
    fun isFullyGrownCrop(blockData: BlockData): Boolean {
        if (blockData !is Ageable) return false
        return blockData.material in configuration.cropMaterials && blockData.age == blockData.maximumAge
    }

    fun replantCrop(cropType: Material, location: Location) {
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            location.block.type = cropType
        }, configuration.replantDelayTicks)
    }
}
