package dev.slne.surf.chat.paper.spy.command

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.paper.spy.spyService
import dev.slne.surf.chat.paper.util.hasPlatformPermission
import dev.slne.surf.cloud.api.client.paper.command.args.onlineCloudPlayerArgument
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.cloud.api.common.player.toCloudPlayer
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun directMessageSpyCommand() = commandAPICommand("spy") {
    withPermission(SurfChatPermissionRegistry.COMMAND_DIRECT_SPY)
    directMessageSpyClearCommand()
    onlineCloudPlayerArgument("target")
    playerExecutor { player, args ->
        val cloudPlayer = player.toCloudPlayer() ?: return@playerExecutor
        val target: CloudPlayer by args

        if (cloudPlayer == target.uuid) {
            player.sendText {
                appendPrefix()
                error("Du kannst dich nicht selbst spionieren!")
            }
            return@playerExecutor
        }

        if (target.hasPlatformPermission(SurfChatPermissionRegistry.TEAM_BYPASS_SPY)) {
            player.sendText {
                appendPrefix()
                error("Du kannst keine Teammitglieder spionieren!")
            }
            return@playerExecutor
        }

        if (spyService.getPrivateMessageSpies(target.uuid).contains(player.uniqueId)) {
            spyService.removePrivateMessageSpy(player.uniqueId, target.uuid)

            player.sendText {
                appendPrefix()
                success("Du spionierst nun ")
                variableValue(target.name)
                success("s private Nachrichten nicht mehr.")
            }
        } else {
            spyService.addPrivateMessageSpy(player.uniqueId, target.uuid)
            player.sendText {
                appendPrefix()
                success("Du spionierst nun ")
                variableValue(target.name)
                success("s private Nachrichten.")
            }
        }
    }
}