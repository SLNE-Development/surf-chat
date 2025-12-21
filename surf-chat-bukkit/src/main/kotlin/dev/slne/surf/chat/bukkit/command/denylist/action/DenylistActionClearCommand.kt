package dev.slne.surf.chat.bukkit.command.denylist.action

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.denylistActionService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.entity.Player

fun CommandAPICommand.denylistActionClearCommand() = subcommand("clear") {
    withPermission(SurfChatPermissionRegistry.COMMAND_DENYLIST_ACTION_CLEAR)
    anyExecutor { executor, _ ->
        if (executor is Player) {
            executor.sendText {
                appendPrefix()
                info("Möchtest du die Denylist Aktionen wirklich leeren? ")
                append {
                    spacer("[")
                    success("Bestätigen")
                    spacer("]")
                    clickEvent(ClickEvent.callback {
                        plugin.launch {
                            denylistActionService.clearActions()
                            denylistActionService.clearLocalActions()
                            executor.sendText {
                                appendPrefix()
                                success("Die Denylist Aktionen wurde geleert.")
                            }
                        }
                    })
                }
            }
            return@anyExecutor
        }

        plugin.launch {
            denylistActionService.clearActions()
            executor.sendText {
                appendPrefix()
                success("Die Denylist Aktionen wurde geleert.")
            }
        }
    }
}