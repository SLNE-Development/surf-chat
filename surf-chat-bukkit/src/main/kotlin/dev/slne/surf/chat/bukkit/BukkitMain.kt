package dev.slne.surf.chat.bukkit

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.api.processor.chatProcessorRegistry
import dev.slne.surf.chat.api.server.ChatServer
import dev.slne.surf.chat.bukkit.config.DiscordConfigProvider
import dev.slne.surf.chat.bukkit.config.SurfChatConfigProvider
import dev.slne.surf.chat.bukkit.processor.post.LogPostChatProcessor
import dev.slne.surf.chat.bukkit.processor.pre.*
import dev.slne.surf.chat.bukkit.processor.pre.validate.CharPreChatProcessor
import dev.slne.surf.chat.bukkit.processor.pre.validate.LinkPreChatProcessor
import dev.slne.surf.chat.bukkit.processor.pre.validate.SpamPreChatProcessor
import dev.slne.surf.chat.core.service.*
import dev.slne.surf.surfapi.bukkit.api.metrics.Metrics
import kotlinx.coroutines.runBlocking
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(BukkitMain::class.java)

lateinit var metrics: Metrics

class BukkitMain : SuspendingJavaPlugin() {
    override fun onLoad() {
        databaseService.establishConnection(plugin.dataPath)
        databaseService.createTables()

        chatProcessorRegistry.register(CharPreChatProcessor)
        chatProcessorRegistry.register(LinkPreChatProcessor)
        chatProcessorRegistry.register(SpamPreChatProcessor)
        chatProcessorRegistry.register(ChannelPreChatProcessor)
        chatProcessorRegistry.register(CorrectViewersPreChatProcessor)
        chatProcessorRegistry.register(FormatPreChatProcessor)
        chatProcessorRegistry.register(IgnorePreChatProcessor)
        chatProcessorRegistry.register(ValidatorPreChatProcessor)

        chatProcessorRegistry.register(LogPostChatProcessor)
    }

    override fun onEnable() {
        BukkitCommandManager.registerCommands()
        BukkitListenerManager.registerBukkitListeners()
        BukkitListenerManager.registerPacketListeners()

        launch {
            denylistService.fetch()
            denylistActionService.fetchActions()
            functionalityService.fetch(server)
        }

        metrics = Metrics(this, 27048)
        redisLoader.connect()
    }

    override fun onDisable() {
        redisLoader.disconnect()

        runBlocking {
            logger.info("Saving online users...")
            userService.onlineUsers.forEach {
                userService.saveUser(it)
            }
            logger.info("Online users saved.")
        }

        databaseService.closeConnection()

        if (::metrics.isInitialized) {
            metrics.shutdown()
        }
    }

    val surfChatConfig = SurfChatConfigProvider()
    val discordConfig = DiscordConfigProvider()
    val connectionMessageConfig get() = surfChatConfig.config.connectionMessageConfig
    val chatMotdConfig get() = surfChatConfig.config.chatMotdConfig
    val chatServerConfig get() = surfChatConfig.config.chatServerConfig
    val autoDisablingConfig get() = surfChatConfig.config.autoDisablingConfig
    val spamConfig get() = surfChatConfig.config.spamConfig

    var server = ChatServer.of(
        chatServerConfig.internalName,
        chatServerConfig.displayName
    )
}