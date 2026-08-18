package dev.slne.surf.chat.paper

import com.google.auto.service.AutoService
import dev.slne.surf.chat.core.client.ChatClientLoader
import dev.slne.surf.chat.core.client.ClientChatInstance
import dev.slne.surf.chat.core.common.ChatInstance
import dev.slne.surf.chat.core.common.rabbit.rpc.ModerationService
import net.kyori.adventure.util.Services

@AutoService(ChatInstance::class)
class PaperChatInstanceImpl : ClientChatInstance, Services.Fallback {
    override val chatClientLoader = ChatClientLoader(plugin.dataPath)
    override val moderationService by lazy {
        rabbitApi.createRpcService<ModerationService>()
    }
}