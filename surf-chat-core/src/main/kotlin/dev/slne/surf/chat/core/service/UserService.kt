package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.entity.User
import org.gradle.internal.impldep.jcifs.dcerpc.UUID

interface UserService {
    fun getUser(uuid: UUID): User?
    fun getUser(name: String): User?
}