package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.model.Channel
import dev.slne.surf.chat.bukkit.command.argument.channelArgument
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.service.spyService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.channelSpyCommand() = subcommand("spy") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_ADMIN_SPY)
    channelArgument("channel")
    channelSpyClearCommand()
    playerExecutor { player, args ->
        val channel: Channel by args
        val user = player.user() ?: return@playerExecutor

        if (channel.isMember(user)) {
            player.sendText {
                appendPrefix()
                error("Du kannst nicht im Nachrichtenkanal ")
                variableValue(channel.channelName)
                error(" spionieren, da du ein Mitglied bist.")
            }
            return@playerExecutor
        }

        if (spyService.getChannelSpies(channel).contains(player.uniqueId)) {
            spyService.removeChannelSpy(player.uniqueId, channel)

            player.sendText {
                appendPrefix()
                success("Du spionierst nicht mehr im Nachrichtenkanal ")
                variableValue(channel.channelName)
                success(" mit.")
            }
        } else {
            spyService.addChannelSpy(player.uniqueId, channel)
            player.sendText {
                appendPrefix()
                success("Du spionierst nun im Nachrichtenkanal ")
                variableValue(channel.channelName)
                success(".")
            }
        }
    }
}