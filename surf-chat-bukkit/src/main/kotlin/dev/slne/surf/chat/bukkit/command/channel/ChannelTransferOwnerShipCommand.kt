package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.stringArgument
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.bukkit.command.argument.ChannelMembersArgument
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.components
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.OfflinePlayer

class ChannelTransferOwnerShipCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(ChannelMembersArgument("member"))
        stringArgument("confirm", optional = true)
        playerExecutor { player, args ->
            val channel: ChannelModel? = channelService.getChannel(player)
            val target = args.getUnchecked<OfflinePlayer>("member") ?: return@playerExecutor
            val confirm = args.getOrDefaultUnchecked("confirm", "")

            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)
                val targetUser = databaseService.getUser(target.uniqueId)

                if (channel == null) {
                    user.sendText(buildText {
                        error("Du bist in keinem Nachrichtenkanal.")
                    })
                    return@launch
                }

                if (!channel.isOwner(user)) {
                    user.sendText(buildText {
                        error("Du bist nicht der Besitzer des Nachrichtenkanals.")
                    })
                    return@launch
                }

                if (!channel.isMember(targetUser)) {
                    user.sendText(buildText {
                        error("Der Spieler ")
                        info(target.name ?: target.uniqueId.toString())
                        error(" ist kein Mitglied in deinem Nachrichtenkanal.")
                    })
                    return@launch
                }

                if (!confirm.equals("confirm", ignoreCase = true) && !confirm.equals("yes", ignoreCase = true) && !confirm.equals("true", ignoreCase = true) && !confirm.equals("ja", ignoreCase = true)) {
                    user.sendText(buildText {
                        primary("Bitte bestätige die Übertragung des Besitzes des Nachrichtenkanals an ")
                        info(targetUser.getName())
                        primary(".")
                        append(components.getTransferConfirmComponent(targetUser.getName()))
                    })
                    return@launch
                }

                channel.transferOwnership(targetUser)

                user.sendText(buildText {
                    primary("Du hast den Nachrichtenkanal ")
                    info(channel.name)
                    primary(" an ")
                    info(target.name ?: target.uniqueId.toString())
                    success(" übertragen.")
                })

                targetUser.sendText(buildText {
                    primary("Du bist jetzt der Besitzer des Nachrichtenkanals ")
                    info(channel.name)
                    success(".")
                })
            }
        }
    }
}
