package dev.slne.surf.chat.core.common.aimoderation

import kotlinx.serialization.Serializable

@Serializable
enum class ModerationCategory {
    HARASSMENT,
    HARASSMENT_THREATENING,
    HATE,
    HATE_THREATENING,
    ILLICIT,
    ILLICIT_VIOLENT,
    SELF_HARM,
    SELF_HARM_INSTRUCTIONS,
    SELF_HARM_INTENT,
    SEXUAL,
    SEXUAL_MINORS,
    VIOLENCE,
    VIOLENCE_GRAPHIC
}