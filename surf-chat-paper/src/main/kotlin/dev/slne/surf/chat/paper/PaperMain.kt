package dev.slne.surf.chat.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.surfapi.bukkit.api.metrics.Metrics
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

lateinit var metrics: Metrics

class PaperMain : SuspendingJavaPlugin() {
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