package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.entity.ChannelMember
import dev.slne.surf.chat.api.model.Channel
import dev.slne.surf.chat.api.model.ChannelRole
import dev.slne.surf.chat.bukkit.command.argument.channelMemberArgument
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.event.ClickEvent

fun CommandAPICommand.channelTransferCommand() = subcommand("transfer") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_TRANSFER)
    channelMemberArgument("member")
    playerExecutor { player, args ->
        val user = player.user() ?: return@playerExecutor
        val channel: Channel = channelService.getChannel(user) ?: run {
            user.sendText {
                appendPrefix()
                error("Du bist in keinem Nachrichtenkanal.")
            }
            return@playerExecutor
        }
        val target: ChannelMember by args

        if (!channel.isOwner(user)) {
            user.sendText {
                appendPrefix()
                error("Du verfügst nicht über die erforderliche Berechtigung.")
            }
            return@playerExecutor
        }

        val targetUser = target.user() ?: run {
            user.sendText {
                appendPrefix()
                error("Der Spieler ist nicht online oder existiert nicht.")
            }
            return@playerExecutor
        }

        if (!channel.isMember(targetUser)) {
            user.sendText {
                appendPrefix()
                error("Der Spieler ")
                variableValue(target.name)
                error(" ist kein Mitglied in deinem Nachrichtenkanal.")
            }
            return@playerExecutor
        }

        user.sendText {
            appendPrefix()
            info("Klicke hier, um den Vorgang zu bestätigen. ")
            success("[BESTÄTIGEN]")
            hoverEvent(buildText {
                info("Klicke hier, um den Vorgang zu bestätigen.")
            })
            clickEvent(ClickEvent.callback {
                if (channel.members.none { it.role == ChannelRole.OWNER }) {
                    user.sendText {
                        appendPrefix()
                        error("Der Nachrichtenkanal benötigt mindestens einen Besitzer.")
                    }
                    return@callback
                }

                channel.transfer(target)

                user.sendText {
                    appendPrefix()
                    success("Du hast den Nachrichtenkanal ")
                    variableValue(channel.channelName)
                    success(" an ")
                    variableValue(target.name)
                    success(" übertragen.")
                }

                targetUser.sendText {
                    appendPrefix()
                    info("Du bist jetzt der Besitzer des Nachrichtenkanals ")
                    variableValue(channel.channelName)
                    info(".")
                }
            })
        }
    }
}
