package dev.slne.surf.chat.minestom

import com.google.auto.service.AutoService
import dev.slne.surf.chat.core.client.ChatClientLoader
import dev.slne.surf.chat.core.client.ClientChatInstance
import dev.slne.surf.chat.core.common.ChatInstance
import dev.slne.surf.chat.core.common.rabbit.rpc.ModerationService
import net.kyori.adventure.util.Services

/**
 * Platform local name for the chat instance backing this plugin.
 */
typealias MinestomChatInstance = ClientChatInstance

@AutoService(ChatInstance::class)
class MinestomChatInstanceImpl : ClientChatInstance, Services.Fallback {
    override val chatClientLoader = ChatClientLoader(SurfChatMinestomEntrypoint.dataPath)
    override val moderationService by lazy {
        rabbitApi.createRpcService<ModerationService>()
    }
}
