package dev.slne.surf.chat.api.entity

import java.util.UUID

interface User {
    val name: String
    val uuid: UUID
}