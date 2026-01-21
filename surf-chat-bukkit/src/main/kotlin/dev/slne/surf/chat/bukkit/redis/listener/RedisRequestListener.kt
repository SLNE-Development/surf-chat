package dev.slne.surf.chat.bukkit.redis.listener

import dev.slne.surf.chat.bukkit.hook.SettingsHook
import dev.slne.surf.chat.bukkit.message.MessageFormatter
import dev.slne.surf.chat.bukkit.redis.request.DirectMessageRequest
import dev.slne.surf.chat.bukkit.redis.response.DirectMessageResponse
import dev.slne.surf.chat.bukkit.util.ignores
import dev.slne.surf.chat.core.service.userService
import dev.slne.surf.redis.request.HandleRedisRequest
import dev.slne.surf.redis.request.RequestContext
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Bukkit
import java.util.*

object RedisRequestListener {
    @HandleRedisRequest
    fun handleDirectMessageRequest(context: RequestContext<DirectMessageRequest>) {
        val target =
            Bukkit.getPlayer(context.request.messageData.receiver?.uuid ?: UUID.randomUUID())
                ?: run {
                    context.respond(DirectMessageResponse(DirectMessageResponse.DirectMessageStatus.USER_NOT_FOUND))
                    return
                }

        val formatter = MessageFormatter()

        val targetUser = userService.findUserByUuid(target.uniqueId) ?: run {
            context.respond(DirectMessageResponse(DirectMessageResponse.DirectMessageStatus.USER_NOT_FOUND))
            return
        }

        if (targetUser.ignores(context.request.messageData.sender.uuid)) {
            context.respond(DirectMessageResponse(DirectMessageResponse.DirectMessageStatus.DIRECT_MESSAGES_DISABLED))
            return
        }

        if (SettingsHook.hasDirectMessagesEnabled(targetUser.uuid)) {
            target.sendText {
                append(formatter.formatIncomingPm(context.request.messageData))
            }
            context.respond(DirectMessageResponse(DirectMessageResponse.DirectMessageStatus.SUCCESS))
            return
        }

        context.respond(DirectMessageResponse(DirectMessageResponse.DirectMessageStatus.DIRECT_MESSAGES_DISABLED))
    }
}