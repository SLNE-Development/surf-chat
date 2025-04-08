package dev.slne.surf.chat.bukkit.util

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

data class LookupFlags(
    val target: OfflinePlayer? = null,
    val type: String? = null,
    val range: Long? = null,
    val message: String? = null,
    val deleted: Boolean? = null,
    val deletedBy: String? = null,
    val page: Int? = null
    ) {
        companion object {
            fun parse(input: String): LookupFlags {
                val regex = Regex("""--(\w+)(?:\s+("[^"]+"|\S+))?""")
                val map = regex.findAll(input).associate {
                    val key = it.groupValues[1]
                    val raw = it.groupValues.getOrNull(2)?.removeSurrounding("\"")?.trim()
                    key to raw
                }

                fun parseRange(range: String?): Long? {
                    if (range == null) return null
                    return when {
                        range.endsWith("d") -> range.removeSuffix("d").toLongOrNull()?.days?.inWholeMilliseconds
                        range.endsWith("h") -> range.removeSuffix("h").toLongOrNull()?.hours?.inWholeMilliseconds
                        range.endsWith("m") -> range.removeSuffix("m").toLongOrNull()?.minutes?.inWholeMilliseconds
                        range.endsWith("s") -> range.removeSuffix("s").toLongOrNull()?.seconds?.inWholeMilliseconds
                        else -> range.toLongOrNull()
                    }
                }

                return LookupFlags(
                    target = map["user"]?.let { Bukkit.getOfflinePlayer(it) },
                    type = map["type"],
                    range = parseRange(map["range"]),
                    message = map["message"],
                    deleted = map["deleted"]?.toBooleanStrictOrNull(),
                    deletedBy = map["deletedBy"],
                    page = map["page"]?.toIntOrNull()
                )
            }
        }

        fun toFlagString(): String {
            val parts = mutableListOf<String>()
            if (target != null) parts += "--user ${target.name ?: target.uniqueId}"
            if (type != null) parts += "--type $type"
            if (range != null) parts += "--range ${range / 1000 / 60 / 60 / 24}d"
            if (message != null) parts += "--message \"$message\""
            if (deleted != null) parts += "--deleted $deleted"
            if (deletedBy != null) parts += "--deletedBy \"$deletedBy\""
            return parts.joinToString(" ")
        }
    }