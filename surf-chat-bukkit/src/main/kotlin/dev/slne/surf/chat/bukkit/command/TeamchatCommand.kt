package dev.slne.surf.chat.bukkit.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.server.ChatServer
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.service.historyService
import net.kyori.adventure.text.Component
import java.util.*

fun teamchatCommand() = commandAPICommand("teamchat", plugin) {
    withAliases("tc")
    greedyStringArgument("message")
    withPermission(SurfChatPermissionRegistry.COMMAND_TEAMCHAT)

    playerExecutor { player, args ->
        val message: String by args
        val messageComponent = Component.text(message)
        val messageId = UUID.randomUUID()
        val messageData = MessageData(
            messageComponent,
            messageId,
            player.user() ?: return@playerExecutor,
            null,
            System.currentTimeMillis(),
            ChatServer.of(
                plugin.chatServerConfig.internalName,
                plugin.chatServerConfig.displayName
            ),
            null,
            null,
            MessageType.TEAM
        )

        // TODO: Send Message (redis)

        plugin.launch {
            historyService.logMessage(messageData)
        }
    }
}