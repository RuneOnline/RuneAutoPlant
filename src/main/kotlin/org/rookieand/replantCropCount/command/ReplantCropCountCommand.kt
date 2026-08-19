package org.rookieand.replantCropCount.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.rookieand.replantCropCount.service.PlayerCountService

@Suppress("UnstableApiUsage", "DEPRECATION")
class ReplantCropCountCommand(
    private val plugin: JavaPlugin,
    private val playerCountService: PlayerCountService
) {
    fun register() {
        plugin.lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
            event.registrar().register(buildNode(), "자동 심기 잔여 횟수 관리", listOf("rcc"))
        }
    }

    private fun buildNode() =
        Commands.literal("replantcropcount")
            .executes { ctx ->
                val sender = ctx.source.sender
                if (sender is Player) {
                    sender.sendMessage("남은 자동 심기 횟수: ${playerCountService.getCount(sender.uniqueId)}")
                } else {
                    sender.sendMessage("사용법: /replantcropcount <get|add|set> <player> [amount]")
                }
                Command.SINGLE_SUCCESS
            }
            .then(
                Commands.literal("get")
                    .requires { it.sender.hasPermission("replantcropcount.admin") }
                    .then(
                        playerArgument().executes { ctx ->
                            handleGet(ctx.source.sender, StringArgumentType.getString(ctx, "player"))
                            Command.SINGLE_SUCCESS
                        }
                    )
            )
            .then(mutateBranch("add"))
            .then(mutateBranch("set"))
            .build()

    private fun mutateBranch(action: String) =
        Commands.literal(action)
            .requires { it.sender.hasPermission("replantcropcount.admin") }
            .then(
                playerArgument().then(
                    Commands.argument("amount", IntegerArgumentType.integer()).executes { ctx ->
                        handleMutate(
                            ctx.source.sender,
                            StringArgumentType.getString(ctx, "player"),
                            IntegerArgumentType.getInteger(ctx, "amount"),
                            action
                        )
                        Command.SINGLE_SUCCESS
                    }
                )
            )

    private fun playerArgument() =
        Commands.argument("player", StringArgumentType.word()).suggests { _, builder ->
            plugin.server.onlinePlayers
                .map { it.name }
                .filter { it.startsWith(builder.remaining, ignoreCase = true) }
                .forEach(builder::suggest)
            builder.buildFuture()
        }

    private fun handleGet(sender: CommandSender, name: String) = runAsync {
        val uuid = Bukkit.getOfflinePlayer(name).uniqueId
        sender.sendMessage("$name 의 잔여 자동 심기 횟수: ${playerCountService.getCount(uuid)}")
    }

    private fun handleMutate(sender: CommandSender, name: String, amount: Int, action: String) = runAsync {
        val uuid = Bukkit.getOfflinePlayer(name).uniqueId
        if (action == "add") playerCountService.addCount(uuid, amount) else playerCountService.setCount(uuid, amount)
        sender.sendMessage("$name 의 잔여 자동 심기 횟수를 갱신했습니다. (현재: ${playerCountService.getCount(uuid)})")
    }

    private fun runAsync(block: () -> Unit) {
        plugin.server.scheduler.runTaskAsynchronously(plugin, Runnable { block() })
    }
}
