package dev.slne.surf.chat.velocity.command

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.api.type.MessageType
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.model.velocityChatFormat
import dev.slne.surf.chat.velocity.plugin
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.chat.velocity.util.toChatUser
import net.kyori.adventure.text.Component
import java.util.*
import kotlin.jvm.optionals.getOrNull

class TeamChatCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_TEAMCHAT)
        greedyStringArgument("message")
        withAliases("tc")
        playerExecutor { player, args ->
            val message = args.getUnchecked<String>("message") ?: return@playerExecutor
            val messageID = UUID.randomUUID()

            container.launch {
                val senderUser = player.toChatUser()

                surfChatApi.logMessage(
                    player.uniqueId,
                    MessageType.TEAM,
                    Component.text(message),
                    messageID,
                    server = player.currentServer.getOrNull()?.serverInfo?.name ?: "Unknown"
                )

                plugin.proxy.allPlayers.filter { it.hasPermission(ChatPermissionRegistry.COMMAND_TEAMCHAT) }.forEach {
                    container.launch {
                        val viewerUser = it.toChatUser()
                        it.sendMessage {
                            velocityChatFormat.format(
                                Component.text(message),
                                senderUser,
                                viewerUser,
                                MessageType.TEAM,
                                "None",
                                messageID,
                                true
                            )
                        }
                    }
                }
            }
        }
    }
}