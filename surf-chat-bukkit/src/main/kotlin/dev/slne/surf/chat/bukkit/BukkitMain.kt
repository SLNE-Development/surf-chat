package dev.slne.surf.chat.bukkit

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.api.processor.chatProcessorRegistry
import dev.slne.surf.chat.bukkit.config.AiModerationConfig
import dev.slne.surf.chat.bukkit.config.SurfChatConfigProvider
import dev.slne.surf.chat.bukkit.listener.AsyncChatListener
import dev.slne.surf.chat.bukkit.listener.ConnectListener
import dev.slne.surf.chat.bukkit.listener.DisconnectListener
import dev.slne.surf.chat.bukkit.processor.post.AiModerationPostChatProcessor
import dev.slne.surf.chat.bukkit.processor.post.LogPostChatProcessor
import dev.slne.surf.chat.bukkit.processor.post.PrivateMessageSpyPostChatProcessor
import dev.slne.surf.chat.bukkit.processor.pre.CorrectViewersPreChatProcessor
import dev.slne.surf.chat.bukkit.processor.pre.FormatPreChatProcessor
import dev.slne.surf.chat.bukkit.processor.pre.IgnorePreChatProcessor
import dev.slne.surf.chat.bukkit.processor.pre.ValidatorPreChatProcessor
import dev.slne.surf.chat.bukkit.processor.pre.validate.CharPreChatProcessor
import dev.slne.surf.chat.bukkit.processor.pre.validate.LinkPreChatProcessor
import dev.slne.surf.chat.bukkit.processor.pre.validate.SpamPreChatProcessor
import dev.slne.surf.chat.core.databaseLoader
import dev.slne.surf.chat.core.service.denylistActionService
import dev.slne.surf.chat.core.service.denylistService
import dev.slne.surf.chat.core.service.functionalityService
import dev.slne.surf.core.api.common.surfCoreApi
import dev.slne.surf.surfapi.bukkit.api.event.register
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(BukkitMain::class.java)

class BukkitMain : SuspendingJavaPlugin() {

    override suspend fun onLoadAsync() {
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
        BukkitCommandManager.registerCommands()

        AsyncChatListener().register()
        DisconnectListener().register()
        ConnectListener.register()

        launch {
            databaseLoader.connect(plugin.dataPath)

            denylistService.fetch()
            denylistActionService.fetchActions()
            functionalityService.fetch(surfCoreApi.getCurrentServerName())
        }
        redisLoader.connect()
    }

    override suspend fun onDisableAsync() {
        redisLoader.disconnect()
        databaseLoader.disconnect()
    }

    val surfChatConfig = SurfChatConfigProvider()
    val connectionMessageConfig get() = surfChatConfig.config.connectionMessageConfig
    val chatMotdConfig get() = surfChatConfig.config.chatMotdConfig
    val autoDisablingConfig get() = surfChatConfig.config.autoDisablingConfig
    val spamConfig get() = surfChatConfig.config.spamConfig
}