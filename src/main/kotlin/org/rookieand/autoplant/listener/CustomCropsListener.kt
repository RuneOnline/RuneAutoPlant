package org.rookieand.autoplant.listener

import net.momirealms.customcrops.api.core.block.BreakReason
import net.momirealms.customcrops.api.event.CropBreakEvent
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.rookieand.autoplant.service.CustomCropsService
import org.rookieand.autoplant.service.PlayerCountService

class CustomCropsListener(
    private val customCropsService: CustomCropsService,
    private val playerCountService: PlayerCountService
) : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBreakCustomCrop(event: CropBreakEvent) {
        if (event.reason() != BreakReason.BREAK) return
        val player = event.entityBreaker() as? Player ?: return
        if (player.gameMode !== GameMode.SURVIVAL) return
        if (playerCountService.getOnlineCount(player.uniqueId) <= 0) return

        if (!customCropsService.tryReplant(player, event)) return
        playerCountService.takeCount(player.uniqueId, 1)
    }
}
