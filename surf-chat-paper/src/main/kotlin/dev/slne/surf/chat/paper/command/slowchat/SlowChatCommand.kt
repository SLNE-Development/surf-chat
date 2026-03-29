package dev.slne.surf.chat.paper.command.slowchat

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.chat.paper.command.argument.NiceToggleArgument
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.chat.paper.service.SlowChatService
import dev.slne.surf.chat.paper.util.realName
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Bukkit

fun slowChatCommand() = commandTree("slowchat") {
    withPermission(PermissionRegistry.SLOW_CHAT_COMMAND)
    literalArgument("#status") {
        anyExecutor { sender, _ ->
            val status = SlowChatService.isSlowChat()

            sender.sendText {
                appendInfoPrefix()
                info("Der Slowchat ist derzeit ")
                variableValue(if (status) "aktiviert" else "deaktiviert")
                info(".")
            }
        }
    }

    argument(NiceToggleArgument("change")) {
        anyExecutor { sender, arguments ->
            val change: Boolean by arguments

            if (change == SlowChatService.isSlowChat()) {
                sender.sendText {
                    appendErrorPrefix()
                    error("Der Slowchat ist bereits ")
                    error(if (change) "aktiviert" else "deaktiviert")
                    error(".")
                }
                return@anyExecutor
            }

            SlowChatService.setSlowChat(change)

            Bukkit.broadcast(buildText {
                appendInfoPrefix()
                info("Der Slowchat wurde von ")
                variableValue(sender.realName())
                appendSpace()
                variableValue(if (change) "aktiviert" else "deaktiviert")
                info(".")
            }, PermissionRegistry.SLOW_CHAT_NOTIFY)

            sender.sendText {
                appendSuccessPrefix()
                success("Der Slowchat wurde erfolgreich ")
                variableValue(if (change) "aktiviert" else "deaktiviert")
                success(".")
            }
        }
    }
}