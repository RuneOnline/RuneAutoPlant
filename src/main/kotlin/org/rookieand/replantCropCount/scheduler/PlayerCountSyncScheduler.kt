package org.rookieand.replantCropCount.scheduler

import org.bukkit.plugin.java.JavaPlugin
import org.rookieand.replantCropCount.configuration.PluginConfiguration
import org.rookieand.replantCropCount.storable.PlayerCountStorable

class PlayerCountSyncScheduler(
    private val plugin: JavaPlugin,
    private val configuration: PluginConfiguration,
    private val storable: PlayerCountStorable
) {
    private var taskId: Int = -1

    fun start() {
        val period = configuration.syncPeriodTicks
        taskId = plugin.server.scheduler.runTaskTimerAsynchronously(plugin, Runnable {
            storable.save()
        }, period, period).taskId
    }

    fun stop() {
        if (taskId != -1) plugin.server.scheduler.cancelTask(taskId)
    }
}
