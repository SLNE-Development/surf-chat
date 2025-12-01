package dev.slne.surf.chat.core.client.result

import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.core.common.message.MessageValidationRequirement
import dev.slne.surf.chat.core.common.util.SyncValues
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import it.unimi.dsi.fastutil.objects.ObjectList
import org.springframework.stereotype.Component
import java.util.*

@Component
class SpamMessageValidationRequirement : MessageValidationRequirement {
    private val messageTimestamps = mutableObject2ObjectMapOf<UUID, ObjectList<Long>>()

    override val sendTeamWarning = false

    override fun test(messageData: MessageData): String? {
        val now = System.currentTimeMillis()
        val interval = SyncValues.spamInterval.get()
        val timestamps =
            messageTimestamps.getOrPut(messageData.sender.uuid) { mutableObjectListOf<Long>() }
                .apply { removeIf { it < now - interval } }

        if (timestamps.size < SyncValues.spamAmount.get()) {
            timestamps += now
            return "Bitte warte einen Moment."
        }

        return null
    }
}