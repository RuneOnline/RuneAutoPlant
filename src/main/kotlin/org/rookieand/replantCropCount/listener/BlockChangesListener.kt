package org.rookieand.replantCropCount.listener

import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.rookieand.replantCropCount.service.CropService
import org.rookieand.replantCropCount.service.PlayerCountService

class BlockChangesListener(
    private val cropService: CropService,
    private val playerCountService: PlayerCountService
) : Listener {

    @EventHandler
    fun onBreakCropBlock(event: BlockBreakEvent) {
        val player = event.player
        if (player.gameMode !== GameMode.SURVIVAL) return
        if (playerCountService.getCount(player.uniqueId) <= 0) return

        val blockData = event.block.blockData
        if (!cropService.isFullyGrownCrop(blockData)) return

        cropService.replantCrop(blockData.material, event.block.location)
        playerCountService.takeCount(player.uniqueId, 1)
    }
}
