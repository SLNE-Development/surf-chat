package dev.slne.surf.chat.minestom.redis.listener

import dev.slne.minestom.lobby.api.extension.ConnectionManager
import dev.slne.minestom.lobby.api.player.getOnlineLobbyPlayerByUuid
import dev.slne.surf.chat.core.client.message.format.formatTeamchat
import dev.slne.surf.chat.core.client.permission.ChatPermissions
import dev.slne.surf.chat.core.client.platform.ChatPlatform
import dev.slne.surf.chat.core.client.redis.event.DeleteRemoteMessageRedisEvent
import dev.slne.surf.chat.core.client.redis.event.TeamMessageRedisEvent
import dev.slne.surf.chat.core.client.redis.event.TeamchatMessageRedisEvent
import dev.slne.surf.chat.core.client.redis.rpc.SendDirectMessageHandledRedisResponse
import dev.slne.surf.chat.core.client.redis.rpc.SendDirectMessageRedisRequest
import dev.slne.surf.chat.minestom.service.DirectMessageService
import dev.slne.surf.redis.event.OnRedisEvent
import dev.slne.surf.redis.request.HandleRedisRequest
import dev.slne.surf.redis.request.RequestContext
import kotlinx.coroutines.launch

/**
 * Handles the chat related redis events and requests of this platform.
 */
object MinestomRedisEventListener {

    @OnRedisEvent
    fun onTeamchatMessage(event: TeamchatMessageRedisEvent) {
        ChatPlatform.launchAsync {
            ChatPlatform.broadcast(
                formatTeamchat(event.messageData),
                ChatPermissions.COMMAND_TEAMCHAT
            )
        }
    }

    @OnRedisEvent
    fun onTeamMessage(event: TeamMessageRedisEvent) {
        ChatPlatform.broadcast(event.message, ChatPermissions.PREFIX_TEAM)
    }

    @OnRedisEvent
    fun handleDeleteRemoteChatMessage(event: DeleteRemoteMessageRedisEvent) {
        ChatPlatform.deleteMessage(event.messageSignature)
    }

    @HandleRedisRequest
    fun handleSendDirectMessageRedisRequest(context: RequestContext<SendDirectMessageRedisRequest>) {
        val (messageData, message) = context.request
        val receiver = messageData.receiver ?: return
        if (ConnectionManager.getOnlineLobbyPlayerByUuid(receiver) == null) return

        context.launch {
            val handled = DirectMessageService.handleRemotePm(messageData, message)
            if (handled) {
                context.respond(SendDirectMessageHandledRedisResponse()).await()
            }
        }
    }
}
