package dev.slne.surf.chat.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.chat.api.processor.chatProcessorRegistry
import dev.slne.surf.chat.core.common.service.functionalityService
import dev.slne.surf.chat.core.paper.PaperChatInstance
import dev.slne.surf.chat.paper.config.AiModerationConfig
import dev.slne.surf.chat.paper.config.SurfChatConfigProvider
import dev.slne.surf.chat.paper.listener.AsyncChatListener
import dev.slne.surf.chat.paper.listener.ConnectListener
import dev.slne.surf.chat.paper.listener.DisconnectListener
import dev.slne.surf.chat.paper.processor.post.AiModerationPostChatProcessor
import dev.slne.surf.chat.paper.processor.post.LogPostChatProcessor
import dev.slne.surf.chat.paper.processor.post.PrivateMessageSpyPostChatProcessor
import dev.slne.surf.chat.paper.processor.pre.CorrectViewersPreChatProcessor
import dev.slne.surf.chat.paper.processor.pre.FormatPreChatProcessor
import dev.slne.surf.chat.paper.processor.pre.IgnorePreChatProcessor
import dev.slne.surf.chat.paper.processor.pre.ValidatorPreChatProcessor
import dev.slne.surf.chat.paper.processor.pre.validate.CharPreChatProcessor
import dev.slne.surf.chat.paper.processor.pre.validate.LinkPreChatProcessor
import dev.slne.surf.chat.paper.processor.pre.validate.SpamPreChatProcessor
import dev.slne.surf.core.api.common.surfCoreApi
import dev.slne.surf.surfapi.bukkit.api.event.register
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

        chatProcessorRegistry.register(LogPostChatProcessor)
        chatProcessorRegistry.register(AiModerationPostChatProcessor)
        chatProcessorRegistry.register(PrivateMessageSpyPostChatProcessor)
    }

    override suspend fun onEnableAsync() {
        PaperChatInstance.paperLoader.onEnable()
        BukkitCommandManager.registerCommands()

        AsyncChatListener().register()
        DisconnectListener().register()
        ConnectListener.register()

        functionalityService.fetch(surfCoreApi.getCurrentServerName())
    }

    override suspend fun onDisableAsync() {
        PaperChatInstance.paperLoader.onDisable()
    }

    val surfChatConfig = SurfChatConfigProvider()
    val connectionMessageConfig get() = surfChatConfig.config.connectionMessageConfig
    val chatMotdConfig get() = surfChatConfig.config.chatMotdConfig
    val autoDisablingConfig get() = surfChatConfig.config.autoDisablingConfig
    val spamConfig get() = surfChatConfig.config.spamConfig
}