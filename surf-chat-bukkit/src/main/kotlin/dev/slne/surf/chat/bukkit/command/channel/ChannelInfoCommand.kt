package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.model.Channel
import dev.slne.surf.chat.api.model.ChannelRole
import dev.slne.surf.chat.api.model.ChannelVisibility
import dev.slne.surf.chat.bukkit.command.argument.channelArgument
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.text.format.TextDecoration

fun CommandAPICommand.channelInfoCommand() = subcommand("info") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_INFO)
    channelArgument("channel", true)
    playerExecutor { player, args ->
        val user = player.user() ?: return@playerExecutor
        val channel: Channel? by args

        if (channel == null) {
            val userChannel = channelService.getChannel(user) ?: run {
                player.sendText {
                    appendPrefix()
                    error("Du bist in keinem Nachrichtenkanal.")
                }
                return@playerExecutor
            }

            player.sendMessage(createInfoMessage(userChannel))
            return@playerExecutor
        }

        player.sendMessage(createInfoMessage(channel ?: run {
            player.sendText {
                appendPrefix()
                error("Du bist in keinem Nachrichtenkanal.")
            }
            return@playerExecutor
        }))
    }
}

private fun createInfoMessage(channel: Channel) = buildText {
    info("Informationen".toSmallCaps()).appendNewline()
    append {
        info("| ")
        decorate(TextDecoration.BOLD)
    }
    spacer("Name: ".toSmallCaps())
    text(channel.channelName, Colors.WHITE).appendNewline()
    append {
        info("| ")
        decorate(TextDecoration.BOLD)
    }
    spacer("Besitzer: ".toSmallCaps())
    text(
        channel.members.firstOrNull { it.role == ChannelRole.OWNER }?.name ?: "Error",
        Colors.WHITE
    )
    appendNewline()
    append {
        info("| ")
        decorate(TextDecoration.BOLD)
    }
    spacer("Modus: ".toSmallCaps())
    text(
        when (channel.visibility) {
            ChannelVisibility.PUBLIC -> "Öffentlich"
            ChannelVisibility.PRIVATE -> "Privat"
        }, Colors.WHITE
    )
    appendNewline()
    append {
        info("| ")
        decorate(TextDecoration.BOLD)
    }
    spacer("Mitglieder: ".toSmallCaps())
    text(channel.members.size, Colors.WHITE)
    appendNewline()
    append {
        info("| ")
        decorate(TextDecoration.BOLD)
    }
    spacer("Einladungen: ".toSmallCaps())
    text(channel.invitedPlayers.size, Colors.WHITE)
}
