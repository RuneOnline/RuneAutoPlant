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
    // 다 자란 작물이고 필요한 씨앗을 인벤토리에 들고 있으면 씨앗 1개를 소모하고 재심기를 예약한다.
    // 재심기가 예약된 경우에만 true를 반환한다(호출 측에서 티켓 차감 여부 판단).
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
