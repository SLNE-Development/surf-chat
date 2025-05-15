package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.bukkit.command.argument.ChannelArgument
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.utils.sendText
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.entity.Player

class ChannelMoveCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_ADMIN_MOVE)
        withArguments(EntitySelectorArgument.OneEntity("player"))
        withArguments(ChannelArgument("channel"))
        withPermission("surf.chat.command.channel.move")

        playerExecutor { player, args ->
            val target = args.getUnchecked<Player>("player") ?: return@playerExecutor
            val channel = args.getUnchecked<ChannelModel>("channel") ?: return@playerExecutor

            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)
                val targetUser = databaseService.getUser(target.uniqueId)

                channelService.move(target, channel)

                user.sendText(buildText {
                    success("Du hast ")
                    variableValue(targetUser.getName())
                    success(" in den Nachrichtenkanal ")
                    variableValue(channel.name)
                    success(" verschoben.")
                })

                targetUser.sendText(buildText {
                    info("Du wurdest in den Nachrichtenkanal ")
                    variableValue(channel.name)
                    info(" verschoben.")
                })
            }
        }
    }
}
