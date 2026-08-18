package dev.slne.surf.chat.minestom.redis.listener

import dev.slne.surf.chat.core.client.message.format.formatTeamchat
import dev.slne.surf.chat.core.client.permission.ChatPermissions
import dev.slne.surf.chat.core.client.platform.ChatPlatform
import dev.slne.surf.chat.core.client.redis.event.DeleteRemoteMessageRedisEvent
import dev.slne.surf.chat.core.client.redis.event.TeamMessageRedisEvent
import dev.slne.surf.chat.core.client.redis.event.TeamchatMessageRedisEvent
import dev.slne.surf.redis.event.OnRedisEvent

/**
 * Handles the chat related redis events of this platform.
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
}
