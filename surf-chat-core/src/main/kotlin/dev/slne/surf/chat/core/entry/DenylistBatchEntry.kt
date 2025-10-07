package dev.slne.surf.chat.core.entry

import dev.slne.surf.chat.api.DenylistAction
import dev.slne.surf.chat.api.entry.DenylistActionType
import dev.slne.surf.chat.api.entry.DenylistEntry
import dev.slne.surf.chat.core.service.denylistActionService
import dev.slne.surf.chat.core.service.denylistService
import kotlin.time.Duration

/**
 * Represents a batch of denylist entries created together.
 *
 * The builder allows fluent creation of multiple denylist entries that share
 * the same metadata (reason, staff, action type, etc.).
 */
class DenylistBatchEntry private constructor(
    val entries: List<DenylistEntry>
) {
    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }

    suspend fun execute() {
        entries.forEach {
            denylistActionService.addAction(it.action)
            denylistActionService.addLocalAction(it.action)
            denylistService.addEntry(
                it.word,
                it.reason,
                it.addedBy,
                it.addedAt,
                it.action
            )
            denylistService.addLocalEntry(
                it.word,
                it.reason,
                it.addedBy,
                it.addedAt,
                it.action
            )
        }
    }

    class Builder {
        private val words = mutableListOf<String>()
        private var reason: String = "No reason specified"
        private var staff: String = "System"
        private var actionType: DenylistActionType = DenylistActionType.WARN
        private var punishReason: String = "No punish reason specified"
        private var duration: Long = 0L

        fun withWords(vararg words: String) = apply {
            this.words.addAll(words)
        }

        fun withReason(reason: String) = apply {
            this.reason = reason
        }

        fun withStaff(staff: String) = apply {
            this.staff = staff
        }

        fun withActionType(actionType: DenylistActionType) = apply {
            this.actionType = actionType
        }

        fun withPunishReason(punishReason: String) = apply {
            this.punishReason = punishReason
        }

        fun withDuration(duration: Duration) = apply {
            this.duration = duration.inWholeMilliseconds
        }

        fun build(): DenylistBatchEntry {
            val now = System.currentTimeMillis()
            val action: DenylistAction = DenylistActionImpl(
                name = "default-${actionType.name.lowercase()}-$duration",
                actionType = actionType,
                reason = punishReason,
                duration = duration
            )

            val entries = words.map { word ->
                DenylistEntryImpl(
                    word = word,
                    reason = reason,
                    addedBy = staff,
                    addedAt = now,
                    action = action
                )
            }

            return DenylistBatchEntry(entries)
        }
    }
}
