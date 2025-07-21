package dev.slne.surf.chat.api.entity

import java.util.*

interface User {
    val name: String
    val uuid: UUID

    fun hasPermission(permission: String): Boolean

    fun configure(): ConfigurableUser
}