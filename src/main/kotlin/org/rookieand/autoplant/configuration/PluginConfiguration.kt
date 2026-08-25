package org.rookieand.autoplant.configuration

import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin
import org.rookieand.autoplant.data.MongoSettings

class PluginConfiguration(plugin: JavaPlugin) {
    private val config = plugin.config

    val mongo: MongoSettings = loadMongo()
    val syncPeriodTicks: Long = loadSyncPeriodTicks()
    val replantDelayTicks: Long = loadReplantDelayTicks()
    val cropSeeds: Map<Material, Material> = loadCropSeeds()
    val stemReplants: Map<Material, Material> = loadStemReplants()
    val customCropsEnabled: Boolean = loadCustomCropsEnabled()

    private fun loadMongo() = MongoSettings(
        uri = config.getString("mongodb.uri") ?: "mongodb://localhost:27017",
        database = config.getString("mongodb.database") ?: "autoplant",
        collection = config.getString("mongodb.collection") ?: "player_counts"
    )

    private fun loadSyncPeriodTicks(): Long =
        config.getLong("sync.period-ticks").takeIf { it > 0 } ?: 1200L

    private fun loadReplantDelayTicks(): Long =
        config.getLong("replant.delay-ticks").takeIf { it > 0 } ?: 20L

    private fun loadCropSeeds(): Map<Material, Material> =
        readMaterialMap("crops").ifEmpty { DEFAULT_CROP_SEEDS }

    private fun loadStemReplants(): Map<Material, Material> =
        readMaterialMap("stems").ifEmpty { DEFAULT_STEMS }

    private fun loadCustomCropsEnabled(): Boolean =
        config.getBoolean("custom-crops.enabled", true)

    private fun readMaterialMap(path: String): Map<Material, Material> {
        val section = config.getConfigurationSection(path) ?: return emptyMap()
        return section.getKeys(false).mapNotNull { key ->
            val from = material(key) ?: return@mapNotNull null
            val to = material(section.getString(key)) ?: return@mapNotNull null
            from to to
        }.toMap()
    }

    private fun material(name: String?): Material? =
        name?.let { runCatching { Material.valueOf(it) }.getOrNull() }

    companion object {
        private val DEFAULT_CROP_SEEDS = mapOf(
            Material.WHEAT to Material.WHEAT_SEEDS,
            Material.CARROTS to Material.CARROT,
            Material.POTATOES to Material.POTATO,
            Material.BEETROOTS to Material.BEETROOT_SEEDS,
            Material.NETHER_WART to Material.NETHER_WART,
            Material.PUMPKIN_STEM to Material.PUMPKIN_SEEDS,
            Material.ATTACHED_PUMPKIN_STEM to Material.PUMPKIN_SEEDS,
            Material.MELON_STEM to Material.MELON_SEEDS,
            Material.ATTACHED_MELON_STEM to Material.MELON_SEEDS,
            Material.COCOA to Material.COCOA_BEANS
        )
        private val DEFAULT_STEMS = mapOf(
            Material.ATTACHED_PUMPKIN_STEM to Material.PUMPKIN_STEM,
            Material.ATTACHED_MELON_STEM to Material.MELON_STEM
        )
    }
}
