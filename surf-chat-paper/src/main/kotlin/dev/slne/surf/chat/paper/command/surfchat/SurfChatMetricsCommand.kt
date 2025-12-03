package dev.slne.surf.chat.paper.command.surfchat

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.core.common.ChatContextHolderImpl
import dev.slne.surf.chat.paper.message.MessageStatisticsService
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.springframework.beans.factory.getBean

private val metricsService by lazy { ChatContextHolderImpl.instance.context.getBean<MessageStatisticsService>() }

fun CommandAPICommand.surfChatMetricsCommand() = subcommand("metrics") {
    withPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_METRICS)
    playerExecutor { player, _ ->
        if (metricsService.receiveStats.contains(player.uniqueId)) {
            metricsService.receiveStats.remove(player.uniqueId)

            player.sendText {
                appendPrefix()
                success("Du bekommst nun keine Chat Statistiken mehr angezeigt.")
            }
        } else {
            metricsService.receiveStats.add(player.uniqueId)

            player.sendText {
                appendPrefix()
                info("Du bekommst nun jede Minute die Chat Statistiken angezeigt.")
            }
        }
    }
}