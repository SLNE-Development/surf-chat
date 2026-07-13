package dev.slne.surf.chat.core.common.aimoderation

import kotlinx.serialization.Serializable

@Serializable
enum class ModerationClassificationAction(vararg categories: ModerationCategory) {
    NONE(
        ModerationCategory.VIOLENCE
    ),
    SILENT_FLAG(
        ModerationCategory.ILLICIT,
        ModerationCategory.ILLICIT_VIOLENT,
        ModerationCategory.SELF_HARM,
        ModerationCategory.SELF_HARM_INSTRUCTIONS,
        ModerationCategory.SELF_HARM_INTENT,
    ),
    DELETE(
        ModerationCategory.HARASSMENT,
        ModerationCategory.HARASSMENT_THREATENING,
        ModerationCategory.HATE,
        ModerationCategory.SEXUAL,
        ModerationCategory.VIOLENCE_GRAPHIC
    ),
    MUTE(ModerationCategory.SEXUAL_MINORS, ModerationCategory.HATE_THREATENING);

    val categories: Set<ModerationCategory> = categories.toSet()

    companion object {
        val reversedEntries = entries.reversed()
    }
}