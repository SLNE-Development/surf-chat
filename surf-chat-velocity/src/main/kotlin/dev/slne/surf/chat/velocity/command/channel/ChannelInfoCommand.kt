package dev.slne.surf.chat.velocity.command.channel

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.channel.ChannelStatus
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.velocity.command.argument.channelArgument
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.chat.velocity.util.toChatUser
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration

class ChannelInfoCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_INFO)
        channelArgument("channel", optional = true)
        playerExecutor { player, args ->
            container.launch {
                val channel = args.getOrDefaultUnchecked<Channel?>(
                    "channel",
                    channelService.getChannel(player.toChatUser())
                ) ?: run {
                    player.sendText {
                        error("Der Kanal existiert nicht oder ist nicht für dich zugänglich.")
                    }
                    return@launch
                }

                player.sendMessage(createInfoMessage(channel))
            }

        }
    }

    private fun createInfoMessage(channel: Channel): Component {
        return buildText {
            info("Informationen".toSmallCaps()).appendNewline()
            append {
                info("| ")
                decorate(TextDecoration.BOLD)
            }.spacer("Name: ".toSmallCaps()).text(channel.name, Colors.WHITE).appendNewline()
            append {
                info("| ")
                decorate(TextDecoration.BOLD)
            }.spacer("Besitzer: ".toSmallCaps()).text(channel.getOwner().name, Colors.WHITE).appendNewline()
            append {
                info("| ")
                decorate(TextDecoration.BOLD)
            }.spacer("Modus: ".toSmallCaps()).text(
                when (channel.status) {
                    ChannelStatus.PUBLIC -> "Öffentlich"
                    ChannelStatus.PRIVATE -> "Privat"
                }, Colors.WHITE
            ).appendNewline()
            append {
                info("| ")
                decorate(TextDecoration.BOLD)
            }.spacer("Mitglieder: ".toSmallCaps()).text(channel.members.size, Colors.WHITE).appendNewline()
            append {
                info("| ")
                decorate(TextDecoration.BOLD)
            }.spacer("Einladungen: ".toSmallCaps()).text(channel.invites.size, Colors.WHITE)
        }
    }
}
