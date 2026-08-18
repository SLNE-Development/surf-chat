package dev.slne.surf.chat.paper.command.direct

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.scope
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.slne.surf.api.core.command.args.awaiting
import dev.slne.surf.api.core.messages.adventure.playSound
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.messages.adventure.text
import dev.slne.surf.api.core.util.mutableObjectSetOf
import dev.slne.surf.api.paper.command.args.asyncSignedMessageArgument
import dev.slne.surf.api.paper.command.executors.playerExecutorSuspend
import dev.slne.surf.api.paper.extensions.server
import dev.slne.surf.api.paper.nms.NmsUseWithCaution
import dev.slne.surf.api.paper.nms.bridges.SurfPaperNmsPlayerBridge
import dev.slne.surf.api.paper.nms.bridges.data.chat.PlayerChatMessageMirror
import dev.slne.surf.api.paper.nms.bridges.data.chat.RemoteChatSessionData
import dev.slne.surf.api.paper.nms.bridges.packets.player.SurfPaperNmsPlayerPackets
import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.core.client.message.format.formatIncomingPm
import dev.slne.surf.chat.core.client.message.format.formatOutgoingPm
import dev.slne.surf.chat.core.client.processor.runPostProcessors
import dev.slne.surf.chat.core.client.processor.runPreProcessors
import dev.slne.surf.chat.core.client.redisApi
import dev.slne.surf.chat.core.client.service.ReplyCache
import dev.slne.surf.chat.core.client.hook.SettingsHook
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.chat.paper.redis.rpc.SendDirectMessageHandledRedisResponse
import dev.slne.surf.chat.paper.redis.rpc.SendDirectMessageRedisRequest
import dev.slne.surf.core.api.common.SurfCoreApi
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.paper.command.argument.surfPlayerArgument
import dev.slne.surf.redis.request.RequestTimeoutException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.chat.SignedMessage
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.time.OffsetDateTime
import java.util.*

fun directMessageCommand() = commandAPICommand("msg") {
    withPermission(PermissionRegistry.COMMAND_PM)
    withAliases("dm", "w", "whisper", "tell", "pm")
    surfPlayerArgument("target")
    asyncSignedMessageArgument("message")

    playerExecutorSuspend { player, args ->
        val target: SurfPlayer by args
        val message = args.awaiting<SignedMessage>("message")

        if (target.uuid == player.uniqueId) {
            player.sendText {
                appendErrorPrefix()
                error("Du kannst dir selbst keine privaten Nachrichten senden!")
            }
            return@playerExecutorSuspend
        }

        DirectMessageAccess.sendMessage(player, message, target.uuid)
    }
}

@OptIn(NmsUseWithCaution::class)
object DirectMessageAccess {
    suspend fun sendMessage(sender: Player, message: SignedMessage, targetUuid: UUID) {
        var messageData = MessageData(
            message.unsignedContent() ?: text(message.message()),
            UUID.randomUUID(),
            sender.uniqueId,
            targetUuid,
            OffsetDateTime.now(),
            SurfCoreApi.getCurrentServerName(),
            null,
            MessageType.DIRECT
        )

        val result = runPreProcessors(MessageContext(messageData, false, mutableObjectSetOf()))
        messageData = result.messageData

        if (!result.isCancelled) {
            SurfPaperNmsPlayerBridge.sendSignedMessageWithChangedContent(
                sender,
                message,
                SurfPaperNmsPlayerBridge.getPaperRawChatType().bind(sender.name()),
                formatOutgoingPm(messageData)
            )

            val target = Bukkit.getPlayer(targetUuid)
            if (target != null) {
                sendSignedPmOnSameServer(sender, target, messageData, message)
            } else {
                sendSignedPmOnDifferentServer(sender, messageData, message)
            }
        } else {
            sender.sendText {
                appendErrorPrefix()
                error("Deine Nachricht konnte nicht zugestellt werden.")
            }
        }

        runPostProcessors(
            MessageContext(
                messageData,
                result.isCancelled,
                mutableObjectSetOf()
            )
        )

        if (!result.isCancelled) {
            coroutineScope {
                launch { ReplyCache.setLastTarget(sender.uniqueId, targetUuid) }
                launch { ReplyCache.setLastTarget(targetUuid, sender.uniqueId) }
            }
        }
    }

    private suspend fun sendSignedPmOnSameServer(
        sender: Player,
        target: Player,
        messageData: MessageData,
        signedMessage: SignedMessage,
    ) {
        if (!preSignedMessageSend(target)) {
            return
        }

        SurfPaperNmsPlayerBridge.sendSignedMessageWithChangedContent(
            target,
            signedMessage,
            SurfPaperNmsPlayerBridge.getPaperRawChatType().bind(sender.displayName()),
            formatIncomingPm(messageData)
        )
    }

    private fun preSignedMessageSend(target: Player): Boolean {
        if (plugin.checkSettingsHook() && !SettingsHook.hasDirectMessagesEnabled(target.uniqueId)) {
            return false
        }

        if (plugin.checkSettingsHook() && SettingsHook.hasChatPingsEnabled(target.uniqueId)) {
            plugin.launch(plugin.entityDispatcher(target)) {
                target.playSound(true) {
                    type(Sound.ENTITY_CHICKEN_EGG)
                }
            }

        }

        return true
    }

    private suspend fun sendSignedPmOnDifferentServer(
        sender: Player,
        messageData: MessageData,
        signedMessage: SignedMessage,
    ) {
        val messageMirror = SurfPaperNmsPlayerBridge.createPlayerChatMessageMirrorFromAdventure(
            signedMessage,
            formatIncomingPm(messageData)
        )

        requireNotNull(messageMirror) { "Failed to create message mirror." }

        SurfPaperNmsPlayerBridge.runOnChatMessageChain(sender, plugin.scope) {
            val session = SurfPaperNmsPlayerBridge.getRemoteChatSessionData(sender)

            try {
                redisApi.sendRequest<SendDirectMessageHandledRedisResponse>(
                    SendDirectMessageRedisRequest(messageData, session, messageMirror),
                )
            } catch (_: RequestTimeoutException) {
            }
        }
    }

    suspend fun handleSendSignedPm(
        data: MessageData,
        messageMirror: PlayerChatMessageMirror,
        senderSession: RemoteChatSessionData?
    ): Boolean {
        val target = data.receiver?.let { Bukkit.getPlayer(it) } ?: return false
        val message = SurfPaperNmsPlayerBridge.createAdventureChatMessageFromMirror(messageMirror)
        if (!preSignedMessageSend(target)) return true

        val senderUser = data.senderUser()
        SurfPaperNmsPlayerPackets.createNewPlayerInfoUpdate(
            data.sender,
            server.createProfile(data.sender, senderUser.username).also { it.completeFromCache() },
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
            SurfPaperNmsPlayerBridge.getPaperRawChatType().bind(text(senderUser.username))
        )

        SurfPaperNmsPlayerPackets.removePlayerInfoUpdate(listOf(data.sender)).execute(target)

        return true
    }
}
