package dev.slne.surf.chat.fallback.repository.history

interface HistoryRepository {
    companion object : HistoryRepository by HistoryRepositoryImpl()
}