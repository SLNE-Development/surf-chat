package dev.slne.surf.chat.paper.message

import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

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

    fun getMessagesPerSecond(): Int = getMessagesPer(1_000)
    fun getMessagesLast10Seconds(): Int = getMessagesPer(10_000)
    fun getMessagesLastMinute(): Int = getMessagesPer(60_000)
}