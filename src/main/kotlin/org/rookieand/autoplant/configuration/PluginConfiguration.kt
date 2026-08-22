package org.rookieand.autoplant.configuration

import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin

class PluginConfiguration(plugin: JavaPlugin) {
    private val config = plugin.config

    val mongoUri: String = config.getString("mongodb.uri") ?: "mongodb://localhost:27017"
    val mongoDatabase: String = config.getString("mongodb.database") ?: "autoplant"
    val mongoCollection: String = config.getString("mongodb.collection") ?: "player_counts"

    val syncPeriodTicks: Long = config.getLong("sync.period-ticks").takeIf { it > 0 } ?: 1200L

    val replantDelayTicks: Long = config.getLong("replant.delay-ticks").takeIf { it > 0 } ?: 20L

    val cropMaterials: Set<Material> = config.getStringList("crops")
        .mapNotNull { runCatching { Material.valueOf(it) }.getOrNull() }
        .toSet()
        .ifEmpty { DEFAULT_CROPS }

    companion object {
        private val DEFAULT_CROPS = setOf(
            Material.WHEAT, Material.CARROT, Material.POTATO, Material.NETHER_WART, Material.BEETROOTS
        )
    }
}
