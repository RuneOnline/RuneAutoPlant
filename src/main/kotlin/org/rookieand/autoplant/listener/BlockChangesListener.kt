package org.rookieand.autoplant.listener

import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.rookieand.autoplant.service.CropService
import org.rookieand.autoplant.service.PlayerCountService

class BlockChangesListener(
    private val cropService: CropService,
    private val playerCountService: PlayerCountService
) : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBreakCropBlock(event: BlockBreakEvent) {
        val player = event.player
        if (player.gameMode !== GameMode.SURVIVAL) return
        if (playerCountService.getCount(player.uniqueId) <= 0) return

        if (!cropService.tryReplant(player, event.block)) return
        playerCountService.takeCount(player.uniqueId, 1)
    }
}
