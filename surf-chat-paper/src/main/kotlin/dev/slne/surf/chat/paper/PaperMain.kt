package dev.slne.surf.chat.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.api.paper.event.register
import dev.slne.surf.api.paper.extensions.pluginManager
import dev.slne.surf.chat.api.processor.chatProcessorRegistry
import dev.slne.surf.chat.core.common.service.FunctionalityService
import dev.slne.surf.chat.core.paper.PaperChatInstance
import dev.slne.surf.chat.paper.command.direct.directMessageCommand
import dev.slne.surf.chat.paper.command.direct.replyCommand
import dev.slne.surf.chat.paper.command.ignore.ignoreCommand
import dev.slne.surf.chat.paper.command.slowchat.slowChatCommand
import dev.slne.surf.chat.paper.command.spy.directMessageSpyCommand
import dev.slne.surf.chat.paper.command.surfchat.surfChatCommand
import dev.slne.surf.chat.paper.command.teamchatCommand
import dev.slne.surf.chat.paper.config.AiModerationConfig
import dev.slne.surf.chat.paper.config.SurfChatConfigProvider
import dev.slne.surf.chat.paper.listener.AsyncChatListener
import dev.slne.surf.chat.paper.listener.ConnectListener
import dev.slne.surf.chat.paper.listener.DisconnectListener
import dev.slne.surf.chat.paper.processor.post.AiModerationPostChatProcessor
import dev.slne.surf.chat.paper.processor.post.LogPostChatProcessor
import dev.slne.surf.chat.paper.processor.post.PrivateMessageSpyPostChatProcessor
import dev.slne.surf.chat.paper.processor.pre.*
import dev.slne.surf.chat.paper.processor.pre.validate.CharPreChatProcessor
import dev.slne.surf.chat.paper.processor.pre.validate.LinkPreChatProcessor
import dev.slne.surf.chat.paper.processor.pre.validate.SpamPreChatProcessor
import dev.slne.surf.chat.paper.redis.listener.RedisEventListener
import dev.slne.surf.core.api.common.SurfCoreApi
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    override suspend fun onLoadAsync() {
        PaperChatInstance.paperLoader.onLoad()
        AiModerationConfig.init()

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
        PaperChatInstance.redisApi.subscribeToEvents(RedisEventListener)
        PaperChatInstance.redisApi.registerRequestHandler(RedisEventListener)
        PaperChatInstance.paperLoader.onEnable()

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
        PaperChatInstance.paperLoader.onDisable()
    }

    fun checkMiniPlaceholdersHook() = pluginManager.isPluginEnabled("MiniPlaceholders")
    fun checkSettingsHook() = pluginManager.isPluginEnabled("surf-settings-paper")

    val surfChatConfig = SurfChatConfigProvider()
    val connectionMessageConfig get() = surfChatConfig.config.connectionMessageConfig
    val chatMotdConfig get() = surfChatConfig.config.chatMotdConfig
    val autoDisablingConfig get() = surfChatConfig.config.autoDisablingConfig
    val spamConfig get() = surfChatConfig.config.spamConfig
}