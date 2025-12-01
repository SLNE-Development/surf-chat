package dev.slne.surf.chat.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.chat.core.common.ChatContextHolderImpl
import dev.slne.surf.chat.core.common.util.SyncValues
import dev.slne.surf.chat.paper.message.MessageStatisticsService
import dev.slne.surf.surfapi.bukkit.api.metrics.Metrics
import org.bukkit.plugin.java.JavaPlugin
import org.springframework.beans.factory.getBean

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

lateinit var metrics: Metrics

class PaperMain : SuspendingJavaPlugin() {
    private val messageStatisticsService by lazy {
        ChatContextHolderImpl.instance.context.getBean<MessageStatisticsService>()
    }

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
}