package dev.slne.surf.chat.minestom

import dev.slne.surf.api.minestom.plugin.SurfMinestomPlugin
import dev.slne.surf.chat.core.client.config.AiModerationConfig
import dev.slne.surf.chat.core.client.config.initChatConfig
import dev.slne.surf.chat.core.common.service.FunctionalityService
import dev.slne.surf.chat.minestom.command.direct.directMessageCommand
import dev.slne.surf.chat.minestom.command.direct.replyCommand
import dev.slne.surf.chat.minestom.command.ignore.ignoreCommand
import dev.slne.surf.chat.minestom.command.slowChatCommand
import dev.slne.surf.chat.minestom.command.spy.directMessageSpyCommand
import dev.slne.surf.chat.minestom.command.surfchat.surfChatCommand
import dev.slne.surf.chat.minestom.command.teamchatCommand
import dev.slne.surf.chat.minestom.listener.ConnectionListener
import dev.slne.surf.chat.minestom.redis.listener.MinestomRedisEventListener
import dev.slne.surf.core.api.common.SurfCoreApi

object SurfChatMinestomPlugin : SurfMinestomPlugin() {
    override suspend fun onLoad() {
        initChatConfig(dataDirectory)
        AiModerationConfig.init(dataDirectory)

        registerProcessors()

        MinestomChatInstance.chatClientLoader.onLoad()

        MinestomChatInstance.redisApi.subscribeToEvents(MinestomRedisEventListener)
        MinestomChatInstance.redisApi.registerRequestHandler(MinestomRedisEventListener)

        MinestomChatInstance.chatClientLoader.onEnable()

        registerChatListener()

        FunctionalityService.fetch(SurfCoreApi.getCurrentServerName())
    }

    override suspend fun onEnable() {
        surfChatCommand()
        teamchatCommand()
        ignoreCommand()
        directMessageSpyCommand()
        slowChatCommand()
        directMessageCommand()
        replyCommand()

        ConnectionListener.register(eventNode)
    }

    override suspend fun onDisable() {
        MinestomChatInstance.chatClientLoader.onDisable()
    }
}
