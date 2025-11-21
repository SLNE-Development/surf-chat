package dev.slne.surf.chat.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.chat.api.server.ChatServer
import dev.slne.surf.chat.core.common.util.SyncValues
import dev.slne.surf.chat.paper.config.DiscordConfigProvider
import dev.slne.surf.chat.paper.config.SurfChatConfigProvider
import dev.slne.surf.surfapi.bukkit.api.metrics.Metrics
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

lateinit var metrics: Metrics

class PaperMain : SuspendingJavaPlugin() {
    init {
        SyncValues.init()
    }

    override fun onEnable() {
        PaperCommandManager.registerCommands()
        PaperListenerManager.registerBukkitListeners()

        metrics = Metrics(this, 27048)
    }

    override fun onDisable() {
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