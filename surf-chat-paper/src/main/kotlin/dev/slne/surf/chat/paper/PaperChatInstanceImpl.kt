package dev.slne.surf.chat.paper

import com.google.auto.service.AutoService
import dev.slne.surf.chat.core.common.ChatInstance
import dev.slne.surf.chat.core.paper.PaperChatInstance
import dev.slne.surf.chat.core.paper.PaperLoader
import net.kyori.adventure.util.Services

@AutoService(ChatInstance::class)
class PaperChatInstanceImpl : PaperChatInstance, Services.Fallback {
    override val paperLoader = PaperLoader(plugin.dataPath)
}