package dev.slne.surf.chat.bukkit.command.surfchat

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor

import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.chatMotdService
import dev.slne.surf.chat.core.service.connectionService
import dev.slne.surf.chat.core.service.messaging.messagingSenderService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

import kotlin.system.measureTimeMillis

class SurfChatReloadCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withAliases("rl")
        withPermission("surf.chat.command.reload")
        playerExecutor { player, _ ->
            val time = measureTimeMillis {
                plugin.reloadConfig()
                messagingSenderService.loadServers()
                plugin.chatFormat.loadServer()
                connectionService.reloadMessages()
                chatMotdService.loadMotd()
            }

            surfChatApi.sendText(player, buildText {
                success("Das Plugin wurde erfolgreich neu geladen.")
                info(" (${time}ms)")
            })
        }
    }
}