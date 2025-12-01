package dev.slne.surf.chat.paper.message

import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import org.bukkit.Bukkit
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit

@Service
class MessageStatisticsService {
    private val messageTimes = ConcurrentLinkedQueue<Long>()
    val receiveStats = mutableObjectSetOf<UUID>()

    fun recordMessage() {
        messageTimes.add(System.currentTimeMillis())
    }

    fun getMessagesPer(lastMillis: Long): Int {
        val cutoff = System.currentTimeMillis() - lastMillis

        while (true) {
            val head = messageTimes.peek() ?: break
            if (head < cutoff) {
                messageTimes.poll()
            } else {
                break
            }
        }

        return messageTimes.size
    }

    @Scheduled(fixedRate = 1L, timeUnit = TimeUnit.MINUTES)
    fun sendMetrics() {
        receiveStats.forEach {
            Bukkit.getPlayer(it)?.sendText {
                appendPrefix()
                info("In der letzten Minute wurden ")
                variableValue(getMessagesPer(60_000))
                info(" Nachrichten empfangen.")
            }
        }
    }
}