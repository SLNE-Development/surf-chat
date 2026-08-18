package dev.slne.surf.chat.minestom.command.spy

import dev.slne.minestom.lobby.api.command.commandapi.CommandAPI
import dev.slne.minestom.lobby.api.command.commandapi.dsl.commandTree
import dev.slne.minestom.lobby.api.command.commandapi.dsl.playerArgument
import dev.slne.minestom.lobby.api.command.commandapi.dsl.playerExecutor
import dev.slne.minestom.lobby.api.player.LobbyPlayer
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.chat.core.client.permission.ChatPermissions
import dev.slne.surf.chat.core.common.service.SpyService
import net.minestom.server.entity.Player

fun directMessageSpyCommand() = commandTree("spy") {
    withPermission(ChatPermissions.COMMAND_DIRECT_SPY)
    directMessageSpyClearCommand()
    playerArgument("target") {
        playerExecutor { player, args ->
            val target: Player by args

            if (player.uuid == target.uuid) {
                CommandAPI.failWithString("Du kannst dich nicht selbst spionieren!")
            }

            if ((target as LobbyPlayer).hasPermission(ChatPermissions.BYPASS_SPY)) {
                player.sendText {
                    appendErrorPrefix()
                    error("Du kannst keine Teammitglieder spionieren!")
                }
                return@playerExecutor
            }

            if (SpyService.getObservingPlayers(target.uuid).contains(player.uuid)) {
                SpyService.removePrivateMessageSpy(player.uuid, target.uuid)

                player.sendText {
                    appendSuccessPrefix()
                    success("Du spionierst nun ")
                    variableValue(target.username)
                    success("s private Nachrichten nicht mehr.")
                }
            } else {
                SpyService.addPrivateMessageSpy(player.uuid, target.uuid)
                player.sendText {
                    appendSuccessPrefix()
                    success("Du spionierst nun ")
                    variableValue(target.username)
                    success("s private Nachrichten.")
                }
            }
        }
    }
}
