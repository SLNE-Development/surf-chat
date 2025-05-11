package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.api.type.ChannelStatusType
import dev.slne.surf.chat.bukkit.command.argument.ChannelArgument
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.text.Component

class ChannelInfoCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withOptionalArguments(ChannelArgument("channel"))
        playerExecutor { player, args ->
            val channel = args.getOrDefaultUnchecked<ChannelModel?>("channel", channelService.getChannel(player))

            if(channel == null) {
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
            appendNewline()
            info("Kanalinformation: ").variableValue(channel.name)
            appendNewline()
            darkSpacer("   - ").variableKey("Besitzer: ")
            variableValue(channel.getOwner().getName())
            appendNewline()
            darkSpacer("   - ").variableKey("Status: ")
            variableValue(when(channel.status) {
                ChannelStatusType.PUBLIC -> "Öffentlich"
                ChannelStatusType.PRIVATE -> "Privat"
            })
            appendNewline()
            darkSpacer("   - ").variableKey("Mitglieder: ")
            variableValue(channel.members.size.toString())
            appendNewline()
            darkSpacer("   - ").variableKey("Einladungen: ")
            variableValue(channel.invites.size.toString())
            appendNewline()
        }
    }
}
