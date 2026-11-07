package dev.slne.surf.chat.paper.debug

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.api.paper.event.register
import dev.slne.surf.api.paper.nms.NmsUseWithCaution
import dev.slne.surf.api.paper.nms.bridges.SurfPaperNmsPlayerBridge
import dev.slne.surf.chat.paper.plugin
import kotlinx.coroutines.delay
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.seconds

/**
 * Whether the server was started with `-Dsurfchat.debug.chatSession=true`.
 *
 * Kept outside [ChatSessionDebugLogger] so that checking it never loads PacketEvents classes.
 */
val isChatSessionDebugEnabled: Boolean
    get() = java.lang.Boolean.getBoolean("surfchat.debug.chatSession")

/**
 * Logs when chat session and chat packets reach this server, and whether the server holds a chat
 * session for each player shortly after they join.
 *
 * Packets are observed without being changed, and message text is never logged. Active only when
 * [isChatSessionDebugEnabled] is true.
 */
@OptIn(NmsUseWithCaution::class)
object ChatSessionDebugLogger : PacketListenerAbstract(PacketListenerPriority.MONITOR), Listener {
    private const val LOG_PREFIX = "[chat-session]"

    /** Pauses between session checks, giving checks 2, 10 and 30 seconds after join. */
    private val checkPauses = listOf(2.seconds, 8.seconds, 20.seconds)

    private val joinTimes = ConcurrentHashMap<UUID, Long>()

    fun enable() {
        PacketEvents.getAPI().eventManager.registerListener(this)
        register()
        plugin.logger.info("$LOG_PREFIX Debug logging is enabled.")
    }

    fun disable() {
        PacketEvents.getAPI().eventManager.unregisterListeners(this)
        HandlerList.unregisterAll(this)
        joinTimes.clear()
    }

    override fun onPacketReceive(event: PacketReceiveEvent) {
        val description = when (event.packetType) {
            PacketType.Play.Client.CHAT_SESSION_UPDATE -> "session packet received"
            PacketType.Play.Client.CHAT_MESSAGE -> "chat packet received"
            else -> return
        }

        val user = event.user
        log(user.uuid, user.name, description)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        joinTimes[player.uniqueId] = System.currentTimeMillis()
        log(player.uniqueId, player.name, "joined")

        plugin.launch {
            for (pause in checkPauses) {
                delay(pause)
                if (Bukkit.getPlayer(player.uniqueId) == null) return@launch

                plugin.launch(plugin.entityDispatcher(player)) {
                    logSessionState(player)
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        log(player.uniqueId, player.name, "left")
        joinTimes.remove(player.uniqueId)
    }

    private fun logSessionState(player: Player) {
        val session = SurfPaperNmsPlayerBridge.getRemoteChatSessionData(player)
        val state = if (session == null) "server has NO chat session" else "server has a chat session"
        log(player.uniqueId, player.name, state)
    }

    private fun log(uuid: UUID?, name: String?, description: String) {
        val joinedAt = uuid?.let(joinTimes::get)
        val timing = joinedAt?.let { "+${System.currentTimeMillis() - it}ms after join" } ?: "before join"
        plugin.logger.info("$LOG_PREFIX ${name ?: uuid ?: "unknown"}: $description ($timing)")
    }
}
