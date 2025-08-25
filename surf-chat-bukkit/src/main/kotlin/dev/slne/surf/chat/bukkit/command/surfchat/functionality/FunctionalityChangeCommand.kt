package dev.slne.surf.chat.bukkit.command.surfchat.functionality

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.server.ChatServer
import dev.slne.surf.chat.bukkit.command.argument.chatServerArgument
import dev.slne.surf.chat.bukkit.command.argument.niceToggleArgument
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.functionalityService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import kotlin.jvm.optionals.getOrNull

fun CommandAPICommand.functionalityChangeCommand() = subcommand("change") {
    withPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_FUNCTIONALITY_TOGGLE)
    niceToggleArgument("toggle")
    chatServerArgument("internalServerName", true)
    anyExecutor { player, args ->
        val toggle: Boolean by args
        val internalServerName: ChatServer? by args
        val server = plugin.server.getOrNull() ?: internalServerName ?: run {
            player.sendText {
                appendPrefix()
                error("Ein interner Fehler ist aufgetreten. Bitte gebe den Server Namen an. /surfchat functionality change <true|false> <internalServerName>")
            }
            return@anyExecutor
        }

        plugin.launch {
            if (toggle) {
                functionalityService.enableLocalChat(server)
                player.sendText {
                    appendPrefix()
                    success("Der Chat wurde aktiviert.")
                }
            } else {
                functionalityService.disableLocalChat(server)
                player.sendText {
                    appendPrefix()
                    success("Der Chat wurde deaktiviert.")
                }
            }
        }
    }
}