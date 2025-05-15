package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.utils.sendText
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.entity.Player

class ChannelUnBanCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_UNBAN)
        withArguments(EntitySelectorArgument.OnePlayer("player"))
        playerExecutor { player, args ->
            val target = args.getUnchecked<Player>("player") ?: return@playerExecutor
            val channel: ChannelModel? = channelService.getChannel(player)

            if(channel == null) {
                surfChatApi.sendText(player, buildText {
                    error("Du bist in keinem Nachrichtenkanal.")
                })
                return@playerExecutor
            }

            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)
                val targetUser = databaseService.getUser(target.uniqueId)

                if (!channel.isModerator(user)) {
                    user.sendText(buildText {
                        error("Du verfügst nicht über die erforderliche Berechtigung.")
                    })
                    return@launch
                }

                if (!channel.isBanned(targetUser)) {
                    user.sendText(buildText {
                        error("Der Spieler ")
                        variableValue(targetUser.getName())
                        error(" ist nicht im Nachrichtenkanal gebannt.")
                    })
                    return@launch
                }

                channel.unban(targetUser)

                user.sendText(buildText {
                    success("Du hast den Spieler ")
                    variableValue(target.name)
                    success(" im Nachrichtenkanal ")
                    variableValue(channel.name)
                    success(" entbannt.")
                })

                targetUser.sendText(buildText {
                    info("Du wurdest im Nachrichtenkanal ")
                    variableValue(channel.name)
                    info(" entbannt.")
                })
            }
        }
    }
}
