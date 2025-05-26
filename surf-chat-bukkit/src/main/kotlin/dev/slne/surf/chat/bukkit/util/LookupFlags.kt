package dev.slne.surf.chat.bukkit.util

import java.util.UUID
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

data class LookupFlags(
    val type: String? = null,
    val range: Long? = null,
    val message: String? = null,
    val deletedBy: String? = null,
    val page: Int? = null,
    val server: String? = null,
    val id: UUID? = null
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
                    range.endsWith("d") -> range.removeSuffix("d")
                        .toLongOrNull()?.days?.inWholeMilliseconds

                    range.endsWith("h") -> range.removeSuffix("h")
                        .toLongOrNull()?.hours?.inWholeMilliseconds

                    range.endsWith("m") -> range.removeSuffix("m")
                        .toLongOrNull()?.minutes?.inWholeMilliseconds

                    range.endsWith("s") -> range.removeSuffix("s")
                        .toLongOrNull()?.seconds?.inWholeMilliseconds

                    else -> range.toLongOrNull()
                }
            }

            return LookupFlags(
                type = map["type"],
                range = parseRange(map["range"]),
                message = map["message"],
                deletedBy = map["deletedBy"],
                page = map["page"]?.toIntOrNull(),
                server = map["server"],
                id = map["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
            )
        }
    }

    fun toFlagString(): String {
        val parts = mutableListOf<String>()
        if (type != null) parts += "--type $type"
        if (range != null) parts += "--range ${range / 1000 / 60 / 60 / 24}d"
        if (message != null) parts += "--message \"$message\""
        if (deletedBy != null) parts += "--deletedBy \"$deletedBy\""
        if (server != null) parts += "--server \"$server\""
        if (id != null) parts += "--id $id"
        return parts.joinToString(" ")
    }
}