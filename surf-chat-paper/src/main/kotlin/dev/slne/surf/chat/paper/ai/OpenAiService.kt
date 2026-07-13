package dev.slne.surf.chat.paper.ai

import com.github.benmanes.caffeine.cache.Caffeine
import com.openai.client.okhttp.OpenAIOkHttpClientAsync
import com.openai.models.moderations.Moderation
import com.openai.models.moderations.ModerationCreateParams
import com.sksamuel.aedile.core.asLoadingCache
import dev.slne.surf.api.core.util.emptyObject2DoubleMap
import dev.slne.surf.chat.core.common.aimoderation.ModerationCategory
import dev.slne.surf.chat.core.common.aimoderation.ModerationClassificationAction
import dev.slne.surf.chat.core.common.aimoderation.ModerationClassificationResult
import dev.slne.surf.chat.paper.config.aiModerationConfig
import it.unimi.dsi.fastutil.objects.Object2DoubleMap
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap
import kotlinx.coroutines.future.await
import java.util.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration

object OpenAiService {
    private val client = OpenAIOkHttpClientAsync.builder()
        .apiKey(aiModerationConfig.apiKey)
        .logLevel(aiModerationConfig.logLevel)
        .build()

    private val resultCache = Caffeine
        .newBuilder()
        .expireAfterWrite(3.hours.toJavaDuration())
        .asLoadingCache<String, ModerationClassificationResult> {
            classifyChatMessage0(it)
        }

    suspend fun classifyChatMessage(message: String): ModerationClassificationResult = resultCache.get(message)

    private suspend fun classifyChatMessage0(message: String): ModerationClassificationResult {
        val params = ModerationCreateParams.builder()
            .input("[GAME_CHAT: MINECRAFT]\n$message")
            .build()

        val result = client.moderations()
            .create(params)
            .await()
            .results()
            .single()

        val flaggedCategoryScores = categoriesByAi(result.categories(), result.categoryScores())

        if (!result.flagged()) {
            return ModerationClassificationResult(
                ModerationClassificationAction.NONE,
                emptyObject2DoubleMap()
            )
        }

        return ModerationClassificationResult(decideAction(flaggedCategoryScores.keys), flaggedCategoryScores)
    }

    private fun decideAction(categories: Set<ModerationCategory>): ModerationClassificationAction {
        for (action in ModerationClassificationAction.reversedEntries) {
            if (action.categories.any { categories.contains(it) }) return action
        }

        return ModerationClassificationAction.NONE
    }


    private fun categoriesByAi(
        categories: Moderation.Categories,
        scores: Moderation.CategoryScores
    ): Object2DoubleMap<ModerationCategory> {
        val map = Object2DoubleOpenHashMap<ModerationCategory>()

        if (categories.harassment()) map.put(ModerationCategory.HARASSMENT, scores.harassment())
        if (categories.harassmentThreatening()) map.put(
            ModerationCategory.HARASSMENT_THREATENING,
            scores.harassmentThreatening()
        )
        if (categories.hate()) map.put(ModerationCategory.HATE, scores.hate())
        if (categories.hateThreatening()) map.put(
            ModerationCategory.HATE_THREATENING,
            scores.hateThreatening()
        )
        categories.illicit().ifTrue { map.put(ModerationCategory.ILLICIT, scores.illicit()) }
        categories.illicitViolent()
            .ifTrue { map.put(ModerationCategory.ILLICIT_VIOLENT, scores.illicitViolent()) }
        if (categories.selfHarm()) map.put(ModerationCategory.SELF_HARM, scores.selfHarm())
        if (categories.selfHarmInstructions()) map.put(
            ModerationCategory.SELF_HARM_INSTRUCTIONS,
            scores.selfHarmInstructions()
        )
        if (categories.selfHarmIntent()) map.put(ModerationCategory.SELF_HARM_INTENT, scores.selfHarmIntent())
        if (categories.sexual()) map.put(ModerationCategory.SEXUAL, scores.sexual())
        if (categories.sexualMinors()) map.put(ModerationCategory.SEXUAL_MINORS, scores.sexualMinors())
        if (categories.violence()) map.put(ModerationCategory.VIOLENCE, scores.violence())
        if (categories.violenceGraphic()) map.put(
            ModerationCategory.VIOLENCE_GRAPHIC,
            scores.violenceGraphic()
        )

        return map
    }

    private inline fun Optional<Boolean>.ifTrue(action: () -> Unit) {
        if (this.orElse(false)) {
            action()
        }
    }
}