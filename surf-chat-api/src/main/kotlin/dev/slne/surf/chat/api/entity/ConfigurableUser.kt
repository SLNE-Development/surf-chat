package dev.slne.surf.chat.api.entity

import java.util.*


interface ConfigurableUser {
    val uuid: UUID

    suspend fun pingsEnabled(): Boolean
    suspend fun enablePings()
    suspend fun disablePings()
}