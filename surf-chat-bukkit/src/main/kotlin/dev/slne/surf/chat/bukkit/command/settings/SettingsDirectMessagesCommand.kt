package dev.slne.surf.chat.bukkit.command.settings

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.bukkit.command.argument.niceToggleArgument
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.settingsDirectMessagesCommand() = subcommand("directMessages") {
    withPermission(SurfChatPermissionRegistry.COMMAND_SETTINGS_DIRECT_MESSAGES)
    niceToggleArgument("newValue", true)

    playerExecutor { player, args ->
        val newValue: Boolean? by args

        plugin.launch {
            val user = player.user() ?: return@launch
            val configurable = user.configure()
            val currentValue = configurable.directMessagesEnabled()

            if (newValue == null) {
                if (currentValue) {
                    configurable.disableDirectMessages()
                } else {
                    configurable.enableDirectMessages()
                }

                player.sendText {
                    appendPrefix()
                    success("Deine Benachrichtigungen wurden ${if (!currentValue) "aktiviert" else "deaktiviert"}.")
                }
            } else {
                if (newValue == currentValue) {
                    player.sendText {
                        appendPrefix()
                        error("Deine Benachrichtigungen sind bereits ${if (newValue == true) "aktiviert" else "deaktiviert"}.")
                    }
                    return@launch
                }

                if (newValue == true) {
                    configurable.enableDirectMessages()
                } else {
                    configurable.disableDirectMessages()
                }

                player.sendText {
                    appendPrefix()
                    success("Deine Benachrichtigungen wurden ${if (newValue == true) "aktiviert" else "deaktiviert"}.")
                }
            }

        }
    }
}