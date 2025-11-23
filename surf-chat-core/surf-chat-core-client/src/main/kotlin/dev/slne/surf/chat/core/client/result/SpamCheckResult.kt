package dev.slne.surf.chat.core.client.result

import dev.slne.surf.chat.api.ChatUuid
import dev.slne.surf.chat.core.common.util.SyncValues
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

        fun of(uuid: ChatUuid): SpamCheckResult {
            val now = System.currentTimeMillis()
            val interval = SyncValues.spamInterval.get()
            val timestamps = messageTimestamps.getOrPut(uuid) { mutableObjectListOf<Long>() }
                .apply { removeIf { it < now - interval } }

            if (timestamps.size < SyncValues.spamAmount.get()) {
                timestamps += now
                return SpamCheckResult(false)
            }

            val min = timestamps.minOrNull() ?: return SpamCheckResult(false)
            val wait = ((min + interval - now).coerceAtLeast(0) / 1000) + 1

            return SpamCheckResult(true, wait)
        }
    }
}