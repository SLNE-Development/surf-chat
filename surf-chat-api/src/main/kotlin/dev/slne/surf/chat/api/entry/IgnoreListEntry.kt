package dev.slne.surf.chat.api.entry

import java.util.*

interface IgnoreListEntry {
    val user: UUID
    val name: String
    val target: UUID
    val targetName: String
    val createdAt: Long
}