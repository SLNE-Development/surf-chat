package dev.slne.surf.chat.paper.command.ignore

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.entry.IgnoreListEntry
import dev.slne.surf.chat.core.client.ignorelist.ignorelistService
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.cloud.api.client.paper.command.args.offlineCloudPlayerArgument
import dev.slne.surf.cloud.api.common.player.OfflineCloudPlayer
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun ignoreCommand() = commandAPICommand("ignore", plugin) {
    withPermission(SurfChatPermissionRegistry.COMMAND_IGNORE)
    ignoreListCommand()
    offlineCloudPlayerArgument("target")
    playerExecutor { player, args ->
        val target: OfflineCloudPlayer by args

        if (player.uniqueId == target.uuid) {
            player.sendText {
                appendPrefix()
                error("Du kannst dich nicht selbst ignorieren.")
            }
            return@playerExecutor
        }

        plugin.launch {
            if (ignorelistService.isIgnoring(player.uniqueId, target.uuid)) {
                ignorelistService.removeFromIgnoreList(player.uniqueId, target.uuid)

                player.sendText {
                    appendPrefix()
                    success("Du ignorierst nun nicht mehr ")
                    variableValue(target.name() ?: "Unbekannt")
                    success(".")
                }
                return@launch
            }

            ignorelistService.addToIgnoreList(
                IgnoreListEntry(
                    player.uniqueId,
                    player.name,
                    target.uuid,
                    target.name() ?: "Unbekannt",
                    System.currentTimeMillis()
                )
            )

            player.sendText {
                appendPrefix()
                success("Du ignorierst nun ")
                variableValue(target.name() ?: "Unbekannt")
                success(".")
            }
        }
    }
}