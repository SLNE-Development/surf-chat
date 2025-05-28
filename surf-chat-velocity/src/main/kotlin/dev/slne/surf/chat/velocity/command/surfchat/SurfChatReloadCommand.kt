package dev.slne.surf.chat.velocity.command.surfchat

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor

import dev.slne.surf.chat.core.service.chatMotdService
import dev.slne.surf.chat.core.service.connectionService
import dev.slne.surf.chat.core.service.filterService
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

import kotlin.system.measureTimeMillis

class SurfChatReloadCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withAliases("rl")
        withPermission(ChatPermissionRegistry.COMMAND_SURFCHAT_RELOAD)
        playerExecutor { player, _ ->
            val time = measureTimeMillis {
                connectionService.reloadMessages()
                chatMotdService.loadMotd()
                filterService.loadDomains()
            }

            player.sendText {
                success("Das Plugin wurde erfolgreich neu geladen.")
                info(" (${time}ms)")
            }
        }
    }
}