package dev.slne.surf.chat.microservice.repository.functionality

import dev.slne.surf.chat.api.functionality.Functionalities

interface FunctionalityRepository {

    suspend fun findByServerOrCreate(server: String): Functionalities
    suspend fun updateOrCreate(server: String, functionalities: Functionalities)
    suspend fun findAll(): Map<String, Functionalities>

    companion object : FunctionalityRepository by FunctionalityRepositoryImpl()
}