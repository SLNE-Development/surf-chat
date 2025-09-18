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

fun CommandAPICommand.settingsInviteCommand() = subcommand("invites") {
    withPermission(SurfChatPermissionRegistry.COMMAND_SETTINGS_INVITES)
    niceToggleArgument("newValue", true)

    playerExecutor { player, args ->
        val newValue: Boolean? by args

        plugin.launch {
            val user = player.user() ?: return@launch
            val configurable = user.configure()

            val currentValue = configurable.invitesEnabled()

            if (newValue == null) {
                if (currentValue) {
                    configurable.disableInvites()
                } else {
                    configurable.enableInvites()
                }

                player.sendText {
                    appendPrefix()
                    if (!currentValue) {
                        success("Kanaleinladungen wurden aktiviert.")
                    } else {
                        success("Kanaleinladungen wurden deaktiviert.")
                    }
                }
            } else {
                if (newValue == currentValue) {
                    player.sendText {
                        appendPrefix()
                        error("Kanaleinladungen sind bereits ${if (newValue == true) "aktiviert" else "deaktiviert"}.")
                    }
                    return@launch
                }

                if (newValue == true) {
                    configurable.enableInvites()
                } else {
                    configurable.disableInvites()
                }

                player.sendText {
                    appendPrefix()

                    if (newValue == true) {
                        success("Kanaleinladungen wurden aktiviert.")
                    } else {
                        success("Kanaleinladungen wurden deaktiviert.")
                    }
                }
            }
        }
    }
}