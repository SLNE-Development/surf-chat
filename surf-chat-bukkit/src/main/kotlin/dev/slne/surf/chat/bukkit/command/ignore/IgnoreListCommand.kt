package dev.slne.surf.chat.bukkit.command.ignore

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.entry.IgnoreListEntry
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.unixTime
import dev.slne.surf.chat.core.service.ignoreService
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.CommonComponents
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.pagination.Pagination
import net.kyori.adventure.text.format.TextDecoration

fun CommandAPICommand.ignoreListCommand() = subcommand("list") {
    withPermission(SurfChatPermissionRegistry.COMMAND_IGNORE_LIST)
    playerExecutor { player, args ->
        player.sendText {
            appendPrefix()
            info("Deine Daten werden geladen, bitte habe einen Moment Geduld...")
        }

        plugin.launch {
            val ignoreList = ignoreService.getIgnoreList(player.uniqueId)

            if (ignoreList.isEmpty()) {
                player.sendText {
                    appendPrefix()
                    error("Du ignorierst aktuell niemanden.")
                }
                return@launch
            }

            val pagination = Pagination<IgnoreListEntry> {
                title {
                    primary("Ignorierte Spieler".toSmallCaps(), TextDecoration.BOLD)
                }

                rowRenderer { entry, _ ->
                    listOf(
                        buildText {
                            append(CommonComponents.EM_DASH)
                            appendSpace()
                            variableKey(entry.targetName)
                            appendSpace()
                            spacer("(${entry.createdAt.unixTime()})")
                        }
                    )
                }
            }

            player.sendText {
                append(pagination.renderComponent(ignoreList))
            }
        }
    }
}