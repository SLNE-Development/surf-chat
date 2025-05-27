package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.core.service.ChatMotdService
import dev.slne.surf.chat.fallback.config.ChatConfig
import dev.slne.surf.surfapi.core.api.config.createSpongeYmlConfig
import dev.slne.surf.surfapi.core.api.config.surfConfigApi
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.util.Services
import java.nio.file.Path

@AutoService(ChatMotdService::class)
class FallbackChatMotdService : ChatMotdService, Services.Fallback {
    var config: ChatConfig? = null

    override fun loadMotd() {
        config = surfConfigApi.getSpongeConfig(ChatConfig::class.java)
    }

    override fun isMotdEnabled(): Boolean {
        return config?.chatMotdConfig?.enabled ?: false
    }

    override fun getMotd(): Component {
        var component = Component.empty()

        config?.chatMotdConfig?.lines?.forEach {
            component = component.append(MiniMessage.miniMessage().deserialize(it))
        }

        return component
    }
}