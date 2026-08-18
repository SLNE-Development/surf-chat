package dev.slne.surf.chat.minestom.command

import dev.slne.minestom.lobby.api.command.commandapi.dsl.anyExecutor
import dev.slne.minestom.lobby.api.command.commandapi.dsl.commandTree
import dev.slne.minestom.lobby.api.command.commandapi.dsl.literalArgument
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.chat.core.client.permission.ChatPermissions
import dev.slne.surf.chat.core.client.platform.ChatPlatform
import dev.slne.surf.chat.core.client.service.SlowChatService
import dev.slne.surf.chat.minestom.command.argument.niceToggleArgument
import net.minestom.server.entity.Player

fun slowChatCommand() = commandTree("slowchat") {
    withPermission(ChatPermissions.SLOW_CHAT_COMMAND)
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

    niceToggleArgument("change") {
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

            ChatPlatform.broadcast(buildText {
                appendInfoPrefix()
                info("Der Slowchat wurde von ")
                variableValue(if (sender is Player) sender.username else "Console")
                appendSpace()
                variableValue(if (change) "aktiviert" else "deaktiviert")
                info(".")
            }, ChatPermissions.SLOW_CHAT_NOTIFY)

            sender.sendText {
                appendSuccessPrefix()
                success("Der Slowchat wurde erfolgreich ")
                variableValue(if (change) "aktiviert" else "deaktiviert")
                success(".")
            }
        }
    }
}
