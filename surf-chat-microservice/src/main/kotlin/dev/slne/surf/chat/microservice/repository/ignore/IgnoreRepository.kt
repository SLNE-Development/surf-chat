package dev.slne.surf.chat.microservice.repository.ignore

import dev.slne.surf.chat.api.entry.IgnoreListEntry
import java.util.*

interface IgnoreRepository {
    suspend fun ignore(uuid: UUID, ignoredUUID: UUID): Boolean
    suspend fun unignore(uuid: UUID, ignoredUUID: UUID): Boolean
    suspend fun findAllByUuid(uuid: UUID): List<IgnoreListEntry>

    companion object : IgnoreRepository by IgnoreRepositoryImpl()
}