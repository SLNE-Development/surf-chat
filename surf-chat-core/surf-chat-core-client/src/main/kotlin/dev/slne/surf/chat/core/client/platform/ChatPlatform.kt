package dev.slne.surf.chat.core.client.platform

import dev.slne.surf.api.core.util.requiredService
import kotlinx.coroutines.CoroutineScope
import net.kyori.adventure.chat.SignedMessage
import net.kyori.adventure.text.Component
import java.util.UUID

private val platform = requiredService<ChatPlatform>()

/**
 * Bridge to the platform specific server implementation used by the shared chat pipeline.
 */
interface ChatPlatform {
    fun hasPermission(uuid: UUID, permission: String): Boolean
    fun sendMessage(uuid: UUID, message: Component)
    fun broadcast(message: Component)
    fun broadcast(message: Component, permission: String)
    fun deleteMessage(signature: SignedMessage.Signature)
    fun onlinePlayerCount(): Int
    fun isOnline(uuid: UUID): Boolean
    fun playPingSound(uuid: UUID)
    fun teleportToPlayer(who: UUID, target: UUID)
    fun launchAsync(block: suspend CoroutineScope.() -> Unit)

    companion object : ChatPlatform by platform
}
