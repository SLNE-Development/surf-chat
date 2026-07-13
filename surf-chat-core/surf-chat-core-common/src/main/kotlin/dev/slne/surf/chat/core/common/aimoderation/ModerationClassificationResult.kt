package dev.slne.surf.chat.core.common.aimoderation

import it.unimi.dsi.fastutil.objects.Object2DoubleMap
import kotlinx.serialization.Serializable

@Serializable
data class ModerationClassificationResult(
    val action: ModerationClassificationAction,
    val flaggedScores: Object2DoubleMap<ModerationCategory>
)