package dev.slne.surf.chat.core.common.aimoderation

import kotlinx.serialization.Serializable

@Serializable
data class ModerationClassificationResult(
    val action: ModerationClassificationAction,
    val flaggedScores: Map<ModerationCategory, Double>
)