package org.rookieand.replantCropCount.service

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.block.data.BlockData
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.plugin.java.JavaPlugin

class CropService(
    private val plugin: JavaPlugin
) {
    private val cropBlockSet =
        setOf(Material.WHEAT, Material.CARROT, Material.POTATO, Material.NETHER_WART, Material.BEETROOTS)
    private val cropSeedMap = mapOf(
        Material.WHEAT to Material.WHEAT_SEEDS,
        Material.POTATO to Material.POTATOES,
        Material.CARROT to Material.CARROTS,
        Material.NETHER_WART to Material.NETHER_WART,
        Material.BEETROOTS to Material.BEETROOT_SEEDS
    )

    fun isFullyGrownCrop(blockData: BlockData): Boolean {
        if (blockData !is Ageable) return false
        return cropBlockSet.contains(blockData.material) && blockData.maximumAge == blockData.age
    }

    fun hasEnoughSeed(cropType: Material, playerInventory: PlayerInventory): Boolean {
        if (!cropBlockSet.contains(cropType)) return false
        val needRemovedSeeds = cropSeedMap[cropType] ?: return false
        return playerInventory.contains(needRemovedSeeds)
    }

    fun takeSeed(cropType: Material, playerInventory: PlayerInventory) {
        val needSeed = cropSeedMap[cropType] ?: return
        if (!playerInventory.contains(needSeed)) return

        for ((index, item) in playerInventory.contents.withIndex()) {
            if (item !is ItemStack || item.type != needSeed) continue
            if (item.amount == 1) {
                playerInventory.setItem(index, null)
            } else {
                item.amount -= 1
                playerInventory.setItem(index, item)
            }
            break
        }
    }

    fun replantCrop(cropType: Material, location: Location) {
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            location.block.type = cropType
        }, 20L)
    }
}
