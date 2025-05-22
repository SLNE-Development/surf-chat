package dev.slne.surf.chat.bukkit.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.api.type.MessageType
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.core.service.messaging.messagingSenderService
import net.kyori.adventure.text.Component
import java.util.*

class TeamChatCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_TEAMCHAT)
        greedyStringArgument("message")
        withAliases("tc")
        playerExecutor { player, args ->
            val message = args.getUnchecked<String>("message") ?: return@playerExecutor
            val messageID = UUID.randomUUID()

            messagingSenderService.sendTeamChatMessage(
                player,
                plugin.chatFormat.formatMessage(
                    Component.text(message),
                    player,
                    player,
                    MessageType.TEAM,
                    "",
                    messageID,
                    true
                )
            )

            plugin.launch {
                surfChatApi.logMessage(
                    player.uniqueId,
                    MessageType.TEAM,
                    Component.text(message),
                    messageID
                )
            }
        }
    }
}