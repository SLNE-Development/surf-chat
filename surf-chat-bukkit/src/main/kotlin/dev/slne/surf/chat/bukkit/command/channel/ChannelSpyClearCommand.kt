package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.core.service.spyService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.channelSpyClearCommand() = subcommand("clear") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_ADMIN_SPY_CLEAR)
    playerExecutor { player, args ->
        if (!spyService.isChannelSpying(player.uniqueId)) {
            player.sendText {
                appendPrefix()
                error("Du spionierst in keinem Kanal.")
            }
            return@playerExecutor
        }

        spyService.clearChannelSpies(player.uniqueId)

        player.sendText {
            appendPrefix()
            success("Du spionierst in keinem Kanal mehr.")
        }
    }
}