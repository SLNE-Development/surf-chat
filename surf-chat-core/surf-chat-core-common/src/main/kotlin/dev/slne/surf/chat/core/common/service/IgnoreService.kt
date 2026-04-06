package dev.slne.surf.chat.core.common.service

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.chat.api.entry.IgnoreListEntry
import java.util.*

private val service = requiredService<IgnoreService>()

interface IgnoreService {

    suspend fun ignore(uuid: UUID, ignoredUUID: UUID): Boolean
    suspend fun unignore(uuid: UUID, ignoredUUID: UUID): Boolean

    fun isIgnored(uuid: UUID, ignoredUUID: UUID): Boolean
    fun getCachedIgnoreList(uuid: UUID): List<IgnoreListEntry>

    suspend fun loadIgnoreList(uuid: UUID): List<IgnoreListEntry>
    suspend fun cleanup(uuid: UUID)

    companion object : IgnoreService by service
}