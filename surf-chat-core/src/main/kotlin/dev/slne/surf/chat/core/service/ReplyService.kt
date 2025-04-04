package dev.slne.surf.chat.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService

interface ReplyService {

    companion object {
        val INSTANCE = requiredService<ReplyService>()
    }
}

val replyService get() = ReplyService.INSTANCE