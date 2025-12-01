package dev.slne.surf.chat.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.chat.core.common.ChatContextHolderImpl
import dev.slne.surf.chat.core.common.util.SyncValues
import dev.slne.surf.chat.paper.message.MessageStatisticsService
import dev.slne.surf.surfapi.bukkit.api.metrics.Metrics
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.springframework.beans.factory.getBean
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

lateinit var metrics: Metrics

@Component
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

    @Scheduled(fixedRate = 1L, timeUnit = TimeUnit.MINUTES)
    fun sendMetrics() {
        messageStatisticsService.receiveStats.forEach {
            Bukkit.getPlayer(it)?.sendText {
                appendPrefix()
                info("In der letzten Minute wurden ")
                variableValue(messageStatisticsService.getMessagesLastMinute())
                info(" Nachrichten empfangen.")
            }
        }
    }
}