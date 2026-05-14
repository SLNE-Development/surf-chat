package dev.slne.surf.chat.paper.redis.listener

import dev.slne.surf.api.paper.nms.NmsUseWithCaution
import dev.slne.surf.chat.paper.command.direct.DirectMessageAccess
import dev.slne.surf.chat.paper.message.MessageFormatter
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.chat.paper.redis.event.TeamMessageRedisEvent
import dev.slne.surf.chat.paper.redis.event.TeamchatMessageRedisEvent
import dev.slne.surf.chat.paper.redis.rpc.SendDirectMessageHandledRedisResponse
import dev.slne.surf.chat.paper.redis.rpc.SendDirectMessageRedisRequest
import dev.slne.surf.chat.paper.redis.rpc.SendSignedMessageRedisRequest
import dev.slne.surf.chat.paper.service.SignedMessageSender
import dev.slne.surf.redis.event.OnRedisEvent
import dev.slne.surf.redis.request.HandleRedisRequest
import dev.slne.surf.redis.request.RequestContext
import kotlinx.coroutines.launch
import org.bukkit.Bukkit

@OptIn(NmsUseWithCaution::class)
object RedisEventListener {
    @OnRedisEvent
    fun onTeamchatMessage(event: TeamchatMessageRedisEvent) {
        val message = MessageFormatter.formatTeamchat(event.messageData)
        Bukkit.broadcast(message, PermissionRegistry.COMMAND_TEAMCHAT)
    }

    @OnRedisEvent
    fun onTeamMessage(event: TeamMessageRedisEvent) {
        val message = event.message
        Bukkit.broadcast(message, PermissionRegistry.PREFIX_TEAM)
    }


    @HandleRedisRequest
    fun handleSendDirectMessageRedisRequest(context: RequestContext<SendDirectMessageRedisRequest>) {
        val (messageData, senderSession, message) = context.request
        val receiver = messageData.receiver ?: return
        if (Bukkit.getPlayer(receiver) == null) return

        context.launch {
            val handled = DirectMessageAccess.handleSendSignedPm(messageData, message, senderSession)
            if (handled) {
                context.respond(SendDirectMessageHandledRedisResponse()).await()
            }
        }
    }

    @HandleRedisRequest
    fun handleSendSignedMessageRedisRequest(context: RequestContext<SendSignedMessageRedisRequest>) = context.launch {
        val handled = SignedMessageSender.handleRemoteSignedMessage(
            context.request.sender,
            context.request.senderName,
            context.request.target,
            context.request.messageMirror,
            context.request.senderSession
        )

        if (handled) {
            context.respond(SendDirectMessageHandledRedisResponse()).await()
        }
    }
}