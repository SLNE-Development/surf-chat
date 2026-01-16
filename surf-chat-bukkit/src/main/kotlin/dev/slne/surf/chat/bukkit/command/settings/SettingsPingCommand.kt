package dev.slne.surf.chat.bukkit.command.settings

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.bukkit.command.argument.niceToggleArgument
import dev.slne.surf.chat.bukkit.permission.PermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.settingsPingCommand() = subcommand("pings") {
    withPermission(PermissionRegistry.COMMAND_SETTINGS_PING)
    niceToggleArgument("newValue", true)

    playerExecutor { player, args ->
        val newValue: Boolean? by args

        plugin.launch {
            val user = player.user() ?: return@launch
            val currentValue = user.chatPingsEnabled

            if (newValue == null) {
                if (currentValue) {
                    user.chatPingsEnabled = false
                } else {
                    user.chatPingsEnabled = true
                }

                player.sendText {
                    appendPrefix()
                    if (!currentValue) {
                        success("Deine Benachrichtigungen wurden aktiviert.")
                    } else {
                        success("Deine Benachrichtigungen wurden deaktiviert.")
                    }
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
                    user.chatPingsEnabled = true
                } else {
                    user.chatPingsEnabled = false
                }

                player.sendText {
                    appendPrefix()

                    if (newValue == true) {
                        success("Deine Benachrichtigungen wurden aktiviert.")
                    } else {
                        success("Deine Benachrichtigungen wurden deaktiviert.")
                    }
                }
            }

        }
    }
}