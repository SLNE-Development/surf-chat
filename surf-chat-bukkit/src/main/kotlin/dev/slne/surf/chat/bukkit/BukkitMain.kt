package dev.slne.surf.chat.bukkit

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.api.server.ChatServer
import dev.slne.surf.chat.bukkit.config.SurfChatConfigProvider
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.core.service.denylistActionService
import dev.slne.surf.chat.core.service.denylistService
import dev.slne.surf.chat.core.service.functionalityService
import dev.slne.surf.surfapi.bukkit.api.metrics.Metrics
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(BukkitMain::class.java)

lateinit var metrics: Metrics

class BukkitMain : SuspendingJavaPlugin() {
    override fun onLoad() {
        databaseService.establishConnection(plugin.dataPath)
        databaseService.createTables()
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
    }

    override fun onDisable() {
        databaseService.closeConnection()

        if (::metrics.isInitialized) {
            metrics.shutdown()
        }
    }

    val surfChatConfig = SurfChatConfigProvider()
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