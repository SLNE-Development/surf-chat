package dev.slne.surf.chat.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.api.paper.event.register
import dev.slne.surf.api.paper.extensions.pluginManager
import dev.slne.surf.chat.api.processor.chatProcessorRegistry
import dev.slne.surf.chat.core.client.ClientChatInstance
import dev.slne.surf.chat.core.client.config.AiModerationConfig
import dev.slne.surf.chat.core.client.config.initChatConfig
import dev.slne.surf.chat.core.common.service.FunctionalityService
import dev.slne.surf.chat.paper.command.direct.directMessageCommand
import dev.slne.surf.chat.paper.command.direct.replyCommand
import dev.slne.surf.chat.paper.command.ignore.ignoreCommand
import dev.slne.surf.chat.paper.command.slowchat.slowChatCommand
import dev.slne.surf.chat.paper.command.spy.directMessageSpyCommand
import dev.slne.surf.chat.paper.command.surfchat.surfChatCommand
import dev.slne.surf.chat.paper.command.teamchatCommand
import dev.slne.surf.chat.paper.listener.AsyncChatListener
import dev.slne.surf.chat.paper.listener.ConnectListener
import dev.slne.surf.chat.paper.listener.DisconnectListener
import dev.slne.surf.chat.core.client.processor.post.AiModerationPostChatProcessor
import dev.slne.surf.chat.core.client.processor.post.LogPostChatProcessor
import dev.slne.surf.chat.core.client.processor.post.PrivateMessageSpyPostChatProcessor
import dev.slne.surf.chat.core.client.processor.pre.IgnorePreChatProcessor
import dev.slne.surf.chat.core.client.processor.pre.SlowChatPreChatProcessor
import dev.slne.surf.chat.core.client.processor.pre.ValidatorPreChatProcessor
import dev.slne.surf.chat.core.client.processor.pre.validate.CharPreChatProcessor
import dev.slne.surf.chat.core.client.processor.pre.validate.LinkPreChatProcessor
import dev.slne.surf.chat.core.client.processor.pre.validate.SpamPreChatProcessor
import dev.slne.surf.chat.paper.processor.pre.CorrectViewersPreChatProcessor
import dev.slne.surf.chat.paper.processor.pre.FormatPreChatProcessor
import dev.slne.surf.chat.core.client.redis.ModerationRedisService
import dev.slne.surf.chat.paper.redis.listener.RedisEventListener
import dev.slne.surf.core.api.common.SurfCoreApi
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    override suspend fun onLoadAsync() {
        initChatConfig(dataPath)
        AiModerationConfig.init(dataPath)

        ClientChatInstance.chatClientLoader.onLoad()

        chatProcessorRegistry.register(CharPreChatProcessor)
        chatProcessorRegistry.register(LinkPreChatProcessor)
        chatProcessorRegistry.register(SpamPreChatProcessor)
        chatProcessorRegistry.register(CorrectViewersPreChatProcessor)
        chatProcessorRegistry.register(FormatPreChatProcessor)
        chatProcessorRegistry.register(IgnorePreChatProcessor)
        chatProcessorRegistry.register(ValidatorPreChatProcessor)
        chatProcessorRegistry.register(SlowChatPreChatProcessor)

        chatProcessorRegistry.register(LogPostChatProcessor)
        chatProcessorRegistry.register(AiModerationPostChatProcessor)
        chatProcessorRegistry.register(PrivateMessageSpyPostChatProcessor)
    }

    override suspend fun onEnableAsync() {
        ClientChatInstance.redisApi.subscribeToEvents(RedisEventListener)
        ClientChatInstance.redisApi.registerRequestHandler(RedisEventListener)
        ClientChatInstance.chatClientLoader.onEnable()

        surfChatCommand()
        teamchatCommand()
        ignoreCommand()
        directMessageSpyCommand()
        directMessageCommand()
        replyCommand()
        slowChatCommand()

        AsyncChatListener.register()
        DisconnectListener.register()
        ConnectListener.register()

        FunctionalityService.fetch(SurfCoreApi.getCurrentServerName())
    }

    override suspend fun onDisableAsync() {
        ClientChatInstance.chatClientLoader.onDisable()
    }

    fun checkMiniPlaceholdersHook() = pluginManager.isPluginEnabled("MiniPlaceholders")
    fun checkSettingsHook() = pluginManager.isPluginEnabled("surf-settings-paper")
}