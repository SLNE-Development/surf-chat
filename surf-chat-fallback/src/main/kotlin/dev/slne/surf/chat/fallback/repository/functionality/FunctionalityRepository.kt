package dev.slne.surf.chat.fallback.repository.functionality

interface FunctionalityRepository {
    companion object : FunctionalityRepository by FunctionalityRepositoryImpl()
}