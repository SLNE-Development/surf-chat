package dev.slne.surf.chat.bukkit.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.bukkit.plugin
import net.kyori.adventure.text.Component
import java.util.*

class TeamChatCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.teamchat")
        greedyStringArgument("message")
        withAliases("tc")
        playerExecutor { player, args ->
            val message = args.getUnchecked<String>("message") ?: return@playerExecutor
            val messageID = UUID.randomUUID()

            plugin.getTeamMembers().forEach {
                surfChatApi.sendRawText(it, plugin.chatFormat.formatMessage(Component.text(message), player, it, ChatMessageType.TEAM, "", messageID))
            }

            plugin.launch {
                surfChatApi.logMessage(player.uniqueId, ChatMessageType.TEAM, Component.text(message), messageID)
            }
        }
    }
}