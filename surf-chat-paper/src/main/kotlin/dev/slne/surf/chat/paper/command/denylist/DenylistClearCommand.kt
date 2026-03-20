package dev.slne.surf.chat.paper.command.denylist

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.chat.core.service.denylistService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.entity.Player

fun CommandAPICommand.denylistClearCommand() = subcommand("clear") {
    withPermission(PermissionRegistry.COMMAND_DENYLIST_CLEAR)
    anyExecutor { executor, _ ->
        if (executor is Player) {
            executor.sendText {
                appendInfoPrefix()
                info("Möchtest du die Denylist wirklich leeren? ")
                append {
                    spacer("[")
                    success("Bestätigen")
                    spacer("]")
                    clickEvent(ClickEvent.callback {
                        plugin.launch {
                            denylistService.clearEntries()
                            denylistService.clearLocalEntries()
                            it.sendText {
                                appendSuccessPrefix()
                                success("Die Denylist wurde geleert.")
                            }
                        }
                    })
                }
            }
            return@anyExecutor
        }

        plugin.launch {
            denylistService.clearEntries()
            executor.sendText {
                appendSuccessPrefix()
                success("Die Denylist wurde geleert.")
            }
        }
    }
}