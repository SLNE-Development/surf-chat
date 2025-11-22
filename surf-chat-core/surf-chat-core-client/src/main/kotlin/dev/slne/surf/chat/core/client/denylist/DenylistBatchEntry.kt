package dev.slne.surf.chat.core.client.denylist

import dev.slne.surf.chat.api.denylist.DenylistAction
import dev.slne.surf.chat.api.denylist.DenylistActionType
import dev.slne.surf.chat.api.denylist.DenylistEntry
import kotlin.time.Duration

/**
 * Represents a batch of denylist entries created together.
 *
 * The builder allows fluent creation of multiple denylist entries that share
 * the same metadata (reason, staff, action type, etc.).
 */
class DenylistBatchEntry private constructor(
    val action: DenylistAction,
    val entries: List<DenylistEntry>
) {
    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }

    fun execute() {
        denylistActionService.addAction(action)
        entries.forEach {
            denylistService.addEntry(
                DenylistEntry(
                    it.word,
                    it.reason,
                    it.addedBy,
                    it.addedAt,
                    it.action
                )
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
            val action = DenylistAction(
                name = "${actionType.name.lowercase()}-$reason",
                actionType = actionType,
                reason = punishReason,
                duration = duration
            )

            val entries = words.map { word ->
                DenylistEntry(
                    word = word,
                    reason = reason,
                    addedBy = staff,
                    addedAt = now,
                    action = action
                )
            }

            return DenylistBatchEntry(action, entries)
        }
    }
}