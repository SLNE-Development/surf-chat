package dev.slne.surf.chat.paper.command.surfchat

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.core.common.netty.packet.serverbound.ServerboundReloadPacket
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.cloud.api.client.netty.packet.fireAndAwait
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.surfChatReloadCommand() = subcommand("reload") {
    withPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_RELOAD)
    anyExecutor { executor, _ ->
        plugin.launch {
            val status = ServerboundReloadPacket().fireAndAwait()?.status == true

            if (status) {
                executor.sendText {
                    appendPrefix()
                    success("Die Konfiguration des Cloud Servers wurde für surf-chat neu geladen.")
                }
            } else {
                executor.sendText {
                    appendPrefix()
                    error("Beim Neuladen der Konfiguration des Cloud Servers für surf-chat ist ein Fehler aufgetreten.")
                }
            }
        }
    }
}