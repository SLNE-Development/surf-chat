package dev.slne.surf.chat.bukkit.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.model.MessageType
import dev.slne.surf.chat.bukkit.message.MessageDataImpl
import dev.slne.surf.chat.bukkit.message.MessageFormatterImpl
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.pluginmessage.pluginMessageSender
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.Constants
import dev.slne.surf.chat.core.service.historyService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import java.util.*
import kotlin.jvm.optionals.getOrNull

fun teamchatCommand() = commandAPICommand("teamchat", plugin) {
    withAliases("tc")
    greedyStringArgument("message")
    withPermission(SurfChatPermissionRegistry.COMMAND_TEAMCHAT)

    playerExecutor { player, args ->
        val message: String by args
        val messageComponent = Component.text(message)
        val messageId = UUID.randomUUID()
        val messageData = MessageDataImpl(
            messageComponent,
            player.user() ?: return@playerExecutor,
            null,
            System.currentTimeMillis(),
            messageId,
            plugin.serverName.getOrNull() ?: "Error",
            null,
            null,
            MessageType.TEAM
        )

        pluginMessageSender(Constants.CHANNEL_TEAM, player) {
            writeUTF(
                GsonComponentSerializer.gson()
                    .serialize(MessageFormatterImpl(messageComponent).formatTeamchat(messageData))
            )
        }

        plugin.launch {
            historyService.logMessage(messageData)
        }
    }
}