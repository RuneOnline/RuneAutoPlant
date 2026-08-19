package org.rookieand.replantCropCount.configuration

import org.bukkit.plugin.java.JavaPlugin

class PluginConfiguration(plugin: JavaPlugin) {
    private val config = plugin.config

    val mongoUri: String = config.getString("mongodb.uri") ?: "mongodb://localhost:27017"
    val mongoDatabase: String = config.getString("mongodb.database") ?: "replant_crop_count"
    val mongoCollection: String = config.getString("mongodb.collection") ?: "player_counts"

    val syncPeriodTicks: Long = config.getLong("sync.period-ticks").takeIf { it > 0 } ?: 1200L
}
