package dev.slne.surf.chat.bukkit.message.result

import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import it.unimi.dsi.fastutil.objects.ObjectList
import java.util.*

data class SpamCheckResult(
    val isSpamming: Boolean,
    val waitSeconds: Long? = null
) {
    companion object {
        private val messageTimestamps = mutableObject2ObjectMapOf<UUID, ObjectList<Long>>()

        fun of(uuid: UUID): SpamCheckResult {
            val now = System.currentTimeMillis()
            val interval = plugin.spamConfig.interval
            val timestamps = messageTimestamps.getOrPut(uuid) { mutableObjectListOf<Long>() }
                .apply { removeIf { it < now - interval } }

            if (timestamps.size < plugin.spamConfig.amount) {
                timestamps += now
                return SpamCheckResult(true)
            }

            val min = timestamps.minOrNull() ?: return SpamCheckResult(false)
            val wait = ((min + interval - now).coerceAtLeast(0) / 1000) + 1

            return SpamCheckResult(false, wait)
        }
    }
}