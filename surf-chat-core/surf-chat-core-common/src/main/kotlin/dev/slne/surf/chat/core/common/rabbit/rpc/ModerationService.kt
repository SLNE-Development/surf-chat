package dev.slne.surf.chat.core.common.rabbit.rpc

import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.core.common.aimoderation.ModerationClassificationResult
import dev.slne.surf.rabbitmq.api.rpc.RpcService

@RpcService
interface ModerationService {
    suspend fun logModeration(
        messageData: MessageData,
        classification: ModerationClassificationResult,
    )
}