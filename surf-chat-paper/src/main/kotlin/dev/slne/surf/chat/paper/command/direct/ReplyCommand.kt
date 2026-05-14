package dev.slne.surf.chat.paper.command.direct

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.api.core.command.args.awaiting
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.paper.command.args.asyncSignedMessageArgument
import dev.slne.surf.api.paper.command.executors.playerExecutorSuspend
import dev.slne.surf.chat.api.SurfChatApi
import dev.slne.surf.chat.core.paper.redisApi
import dev.slne.surf.chat.paper.message.MessageFormatter
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.core.api.common.SurfCoreApi
import dev.slne.surf.redis.codec.UUIDCodec
import dev.slne.surf.redis.libs.redisson.api.RMapCacheReactive
import kotlinx.coroutines.reactor.awaitSingleOrNull
import net.kyori.adventure.chat.SignedMessage
import java.util.*
import java.util.concurrent.TimeUnit


object ReplyCache {
    private val lastMessages: RMapCacheReactive<UUID, UUID> by lazy {
        redisApi.redissonReactive.getMapCache("surf-chat:last-reply-cache", UUIDCodec.INSTANCE)
    }

    suspend fun getLastTarget(uuid: UUID) = lastMessages
        .get(uuid)
        .awaitSingleOrNull()

    suspend fun setLastTarget(uuid: UUID, target: UUID) = lastMessages
        .put(uuid, target, 15, TimeUnit.MINUTES)
        .awaitSingleOrNull()
}

fun replyCommand() = commandAPICommand("reply") {
    withPermission(PermissionRegistry.COMMAND_REPLY)
    withAliases("r")
    asyncSignedMessageArgument("message")

    playerExecutorSuspend { player, args ->
        val message = args.awaiting<SignedMessage>("message")
        val lastMessagedUuid = ReplyCache.getLastTarget(player.uniqueId)

        if (lastMessagedUuid == null) {
            player.sendText {
                appendErrorPrefix()
                error("Du hast noch keine privaten Nachrichten erhalten, auf die du antworten könntest.")
            }
            return@playerExecutorSuspend
        }

        if (lastMessagedUuid == player.uniqueId) {
            player.sendText {
                appendErrorPrefix()
                error("Du kannst dir selbst keine privaten Nachrichten senden!")
            }
            return@playerExecutorSuspend
        }

        val lastMessagedPlayer = SurfCoreApi.getPlayer(lastMessagedUuid)

        if (lastMessagedPlayer == null) {
            player.sendText {
                appendErrorPrefix()
                error("Der Spieler ist nicht mehr online.")
            }
            return@playerExecutorSuspend
        }

        SurfChatApi.sendSignedMessage(
            player.uniqueId,
            message,
            listOf(lastMessagedUuid),
            outgoingFormatter = { MessageFormatter.formatOutgoingPm(it) },
            incomingFormatter = { MessageFormatter.formatIncomingPm(it) }
        )
    }
}