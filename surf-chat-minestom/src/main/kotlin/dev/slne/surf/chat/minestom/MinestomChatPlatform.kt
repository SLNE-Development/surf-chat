package dev.slne.surf.chat.minestom

import com.google.auto.service.AutoService
import dev.slne.minestom.lobby.api.coroutine.minestomAsyncScope
import dev.slne.minestom.lobby.api.extension.ConnectionManager
import dev.slne.minestom.lobby.api.player.LobbyPlayer
import dev.slne.minestom.lobby.api.player.getOnlineLobbyPlayerByUuid
import dev.slne.surf.api.core.messages.adventure.sound
import dev.slne.surf.chat.core.client.platform.ChatPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.chat.SignedMessage
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.minestom.server.adventure.audience.Audiences
import net.minestom.server.sound.SoundEvent
import net.minestom.server.sound.SoundEventKeys
import java.util.UUID

@AutoService(ChatPlatform::class)
class MinestomChatPlatform : ChatPlatform {
    override fun hasPermission(uuid: UUID, permission: String) =
        ConnectionManager.getOnlineLobbyPlayerByUuid(uuid)?.hasPermission(permission) ?: false

    override fun sendMessage(uuid: UUID, message: Component) {
        ConnectionManager.getOnlineLobbyPlayerByUuid(uuid)?.sendMessage(message)
    }

    override fun broadcast(message: Component) {
        Audiences.players().sendMessage(message)
        Audiences.console().sendMessage(message)
    }

    override fun broadcast(message: Component, permission: String) {
        Audiences.players { (it as? LobbyPlayer)?.hasPermission(permission) == true }
            .sendMessage(message)
        Audiences.console().sendMessage(message)
    }

    override fun deleteMessage(signature: SignedMessage.Signature) {
        Audiences.players().deleteMessage(signature)
    }

    override fun onlinePlayerCount() = ConnectionManager.onlinePlayerCount

    override fun isOnline(uuid: UUID) = ConnectionManager.getOnlinePlayerByUuid(uuid) != null

    override fun playPingSound(uuid: UUID) {
        val player = ConnectionManager.getOnlineLobbyPlayerByUuid(uuid) ?: return

        player.playSound(PING_SOUND, Sound.Emitter.self())
    }

    override fun teleportToPlayer(who: UUID, target: UUID) {
        val whoPlayer = ConnectionManager.getOnlineLobbyPlayerByUuid(who) ?: return
        val targetPlayer = ConnectionManager.getOnlineLobbyPlayerByUuid(target) ?: return

        whoPlayer.teleport(targetPlayer.position)
    }

    override fun launchAsync(block: suspend CoroutineScope.() -> Unit) {
        minestomAsyncScope.launch { block() }
    }

    companion object {
        private val PING_SOUND = sound {
            type(SoundEvent.ENTITY_CHICKEN_EGG)
            source(Sound.Source.PLAYER)
        }
    }
}
