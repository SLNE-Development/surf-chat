package dev.slne.surf.chat.api.type

enum class MessageValidationResult {
    SUCCESS,
    FAILED_MUTED,
    FAILED_SPAM,
    FAILED_SELF,
    FAILED_BAD_LINK,
    FAILED_BAD_WORD,
    FAILED_BAD_CHARACTER,
    FAILED_PM_DISABLED,
    FAILED_BLACKLIST,
    FAILED_IGNORING,
}