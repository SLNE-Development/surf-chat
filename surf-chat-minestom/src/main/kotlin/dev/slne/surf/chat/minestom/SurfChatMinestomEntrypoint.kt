package dev.slne.surf.chat.minestom

import com.google.inject.Inject
import com.google.inject.Singleton
import dev.slne.minestom.lobby.api.plugin.MinestomPluginEntrypoint
import dev.slne.minestom.lobby.api.plugin.annotation.DataDirectory
import dev.slne.surf.chat.core.client.config.AiModerationConfig
import dev.slne.surf.chat.core.client.config.initChatConfig
import dev.slne.surf.chat.core.common.service.FunctionalityService
import dev.slne.surf.chat.minestom.redis.listener.MinestomRedisEventListener
import dev.slne.surf.core.api.common.SurfCoreApi
import java.nio.file.Path

@Singleton
class SurfChatMinestomEntrypoint @Inject constructor(
    @DataDirectory path: Path,
) : MinestomPluginEntrypoint {

    init {
        dataPath = path
    }

    override suspend fun start() {
        initChatConfig(dataPath)
        AiModerationConfig.init(dataPath)

        registerProcessors()

        MinestomChatInstance.chatClientLoader.onLoad()

        MinestomChatInstance.redisApi.subscribeToEvents(MinestomRedisEventListener)

        MinestomChatInstance.chatClientLoader.onEnable()

        registerChatListener()

        FunctionalityService.fetch(SurfCoreApi.getCurrentServerName())
    }

    override suspend fun stop() {
        MinestomChatInstance.chatClientLoader.onDisable()
    }

    companion object {
        lateinit var dataPath: Path
    }
}
