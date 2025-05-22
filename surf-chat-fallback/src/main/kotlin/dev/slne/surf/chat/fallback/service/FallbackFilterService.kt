package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.model.ChatUser
import dev.slne.surf.chat.api.type.MessageValidationResult
import dev.slne.surf.chat.core.service.FilterService
import net.kyori.adventure.text.Component
import net.kyori.adventure.util.Services
import java.util.UUID

@AutoService(FilterService::class)
class FallbackFilterService : FilterService, Services.Fallback {
    override fun find(
        message: Component,
        user: ChatUser
    ): MessageValidationResult {
        TODO("Not yet implemented")
    }

    override fun containsLink(message: Component): Boolean {
        TODO("Not yet implemented")
    }

    override fun isValidInput(input: Component): Boolean {
        TODO("Not yet implemented")
    }

    override fun isSpamming(uuid: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override fun setMessageLimit(seconds: Int, count: Int) {
        TODO("Not yet implemented")
    }

    override fun getMessageLimit(): Pair<Int, Int> {
        TODO("Not yet implemented")
    }

    override fun loadDomains() {
        TODO("Not yet implemented")
    }

    override fun loadMessageLimit() {
        TODO("Not yet implemented")
    }

    override fun saveMessageLimit() {
        TODO("Not yet implemented")
    }
}