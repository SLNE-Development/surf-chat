package dev.slne.surf.chat.paper.processor.post

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PostChatProcessor
import dev.slne.surf.chat.core.common.netty.packet.serverbound.history.ServerboundHistoryLogPacket
import dev.slne.surf.cloud.api.client.netty.packet.fireAndForget
import org.springframework.stereotype.Component

@Component
class LogPostChatProcessor : PostChatProcessor {
    override suspend fun process(messageContext: MessageContext) {
        ServerboundHistoryLogPacket(messageContext.messageData).fireAndForget()
    }
}