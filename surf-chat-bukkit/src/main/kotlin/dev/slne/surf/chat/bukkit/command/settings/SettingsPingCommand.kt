package dev.slne.surf.chat.bukkit.command.settings

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.bukkit.command.argument.niceToggleArgument
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun settingsPingCommand() = subcommand("settings") {
    withPermission(SurfChatPermissionRegistry.COMMAND_SETTINGS)
    niceToggleArgument("newValue", true)

    playerExecutor { player, args ->
        val newValue: Boolean? by args

        plugin.launch {
            val user = player.user() ?: return@launch
            val configurable = user.configure()

            if (newValue == null) {
                val currentValue = configurable.pingsEnabled()

                if (currentValue) {
                    configurable.disablePings()
                } else {
                    configurable.enablePings()
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
                if (newValue == true) {
                    configurable.enablePings()
                } else {
                    configurable.disablePings()
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