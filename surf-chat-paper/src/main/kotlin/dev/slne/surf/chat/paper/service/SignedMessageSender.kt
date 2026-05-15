package dev.slne.surf.chat.paper.service

import com.github.shynixn.mccoroutine.folia.scope
import dev.slne.surf.api.core.messages.adventure.text
import dev.slne.surf.api.paper.extensions.server
import dev.slne.surf.api.paper.nms.NmsUseWithCaution
import dev.slne.surf.api.paper.nms.bridges.SurfPaperNmsPlayerBridge
import dev.slne.surf.api.paper.nms.bridges.data.chat.PlayerChatMessageMirror
import dev.slne.surf.api.paper.nms.bridges.data.chat.RemoteChatSessionData
import dev.slne.surf.api.paper.nms.bridges.packets.player.SurfPaperNmsPlayerPackets
import dev.slne.surf.chat.core.paper.redisApi
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.chat.paper.redis.rpc.SendSignedMessageHandledRedisResponse
import dev.slne.surf.chat.paper.redis.rpc.SendSignedMessageRedisRequest
import dev.slne.surf.core.api.common.SurfCoreApi
import dev.slne.surf.redis.request.RequestTimeoutException
import net.kyori.adventure.chat.SignedMessage
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import java.util.*

@OptIn(NmsUseWithCaution::class)
object SignedMessageSender {
    fun sendLocalSignedMessage(
        sender: Player,
        target: Player,
        component: Component,
        signedMessage: SignedMessage,
    ) {
        SurfPaperNmsPlayerBridge.sendSignedMessageWithChangedContent(
            target,
            signedMessage,
            SurfPaperNmsPlayerBridge.getPaperRawChatType().bind(sender.displayName()),
            component
        )
    }

    fun sendRemoteSignedMessage(
        sender: Player,
        targetUuid: UUID,
        component: Component,
        signedMessage: SignedMessage,
    ) {
        val messageMirror = SurfPaperNmsPlayerBridge.createPlayerChatMessageMirrorFromAdventure(
            signedMessage,
            component
        )

        requireNotNull(messageMirror) { "Failed to create message mirror." }

        SurfPaperNmsPlayerBridge.runOnChatMessageChain(sender, plugin.scope) {
            val session = SurfPaperNmsPlayerBridge.getRemoteChatSessionData(sender)

            try {
                redisApi.sendRequest<SendSignedMessageHandledRedisResponse>(
                    SendSignedMessageRedisRequest(sender.uniqueId, sender.name, targetUuid, messageMirror, session),
                )
            } catch (_: RequestTimeoutException) {
            }
        }
    }

    fun handleRemoteSignedMessage(
        sender: UUID,
        senderName: String,
        target: UUID,
        messageMirror: PlayerChatMessageMirror,
        senderSession: RemoteChatSessionData?
    ): Boolean {
        if (SurfCoreApi.getPlayer(target) == null) {
            return true
        }

        val target = Bukkit.getPlayer(target) ?: return false
        val message = SurfPaperNmsPlayerBridge.createAdventureChatMessageFromMirror(messageMirror)

        SurfPaperNmsPlayerPackets.createNewPlayerInfoUpdate(
            sender,
            server.createProfile(sender, senderName).also { it.completeFromCache() },
            false,
            30,
            GameMode.ADVENTURE,
            null,
            false,
            1,
            senderSession
        ).execute(target)

        SurfPaperNmsPlayerBridge.sendPlayerChatMessage(
            target,
            message,
            SurfPaperNmsPlayerBridge.getPaperRawChatType().bind(text(senderName))
        )

        SurfPaperNmsPlayerPackets.removePlayerInfoUpdate(listOf(sender)).execute(target)

        return true
    }
}