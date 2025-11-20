package dev.slne.surf.chat.paper.command.ignore

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.core.common.service.ignoreService
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.cloud.api.client.paper.command.args.onlineCloudPlayerArgument
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun ignoreCommand() = commandAPICommand("ignore", plugin) {
    withPermission(SurfChatPermissionRegistry.COMMAND_IGNORE)
    ignoreListCommand()
    onlineCloudPlayerArgument("target")
    playerExecutor { player, args ->
        val target: CloudPlayer by args

        if (player.uniqueId == target.uuid) {
            player.sendText {
                appendPrefix()
                error("Du kannst dich nicht selbst ignorieren.")
            }
            return@playerExecutor
        }

        plugin.launch {
            if (ignoreService.isIgnored(player.uniqueId, target.uuid)) {
                ignoreService.unIgnore(player.uniqueId, target.uuid)

                player.sendText {
                    appendPrefix()
                    success("Du ignorierst nun nicht mehr ")
                    variableValue(target.name)
                    success(".")
                }
                return@launch
            }

            ignoreService.ignore(player.uniqueId, player.name, target.uuid, target.name)

            player.sendText {
                appendPrefix()
                success("Du ignorierst nun ")
                variableValue(target.name)
                success(".")
            }
        }
    }
}