package dev.slne.surf.chat.api.model

interface BlacklistWordModel {
    val word: String
    val reason: String

    val addedAt: Long
    val addedBy: String
}