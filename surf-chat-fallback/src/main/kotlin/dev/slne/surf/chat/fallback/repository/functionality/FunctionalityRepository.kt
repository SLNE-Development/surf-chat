package dev.slne.surf.chat.fallback.repository.functionality

import dev.slne.surf.chat.core.functionality.Functionalities

interface FunctionalityRepository {

    suspend fun findByServerOrCreate(server: String): Functionalities
    suspend fun updateOrCreate(server: String, functionalities: Functionalities)
    suspend fun findAll(): Map<String, Functionalities>

    companion object : FunctionalityRepository by FunctionalityRepositoryImpl()
}