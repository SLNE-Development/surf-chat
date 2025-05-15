package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.api.type.ChannelStatusType
import dev.slne.surf.chat.bukkit.command.argument.ChannelArgument
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration

class ChannelInfoCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_INFO)
        withOptionalArguments(ChannelArgument("channel"))
        playerExecutor { player, args ->
            val channel = args.getOrDefaultUnchecked<ChannelModel?>(
                "channel",
                channelService.getChannel(player)
            ) ?: run {
                player.sendText {
                    error("Der Kanal existiert nicht oder ist nicht für dich zugänglich.")
                }
                return@playerExecutor
            }

            player.sendMessage(createInfoMessage(channel))
        }
    }

    private fun createInfoMessage(channel: ChannelModel): Component {
        return buildText {
            info("Informationen".toSmallCaps()).appendNewline()
            append {
                info("| ")
                decorate(TextDecoration.BOLD)
            }.spacer("Name: ".toSmallCaps()).text(channel.name, Colors.WHITE).appendNewline()
            append {
                info("| ")
                decorate(TextDecoration.BOLD)
            }.spacer("Besitzer: ".toSmallCaps()).text(channel.getOwner().getName(), Colors.WHITE).appendNewline()
            append {
                info("| ")
                decorate(TextDecoration.BOLD)
            }.spacer("Modus: ".toSmallCaps()).text(
                when (channel.status) {
                    ChannelStatusType.PUBLIC -> "Öffentlich"
                    ChannelStatusType.PRIVATE -> "Privat"
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
