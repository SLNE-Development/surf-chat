package dev.slne.surf.chat.paper.ai

import com.github.benmanes.caffeine.cache.Caffeine
import com.openai.client.okhttp.OpenAIOkHttpClientAsync
import com.openai.models.moderations.Moderation
import com.openai.models.moderations.ModerationCreateParams
import com.sksamuel.aedile.core.asLoadingCache
import dev.slne.surf.api.core.util.emptyObject2DoubleMap
import dev.slne.surf.chat.paper.config.aiModerationConfig
import it.unimi.dsi.fastutil.objects.Object2DoubleMap
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap
import kotlinx.coroutines.future.await
import java.util.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration

val openAiService = OpenAiService()

class OpenAiService {
    private val client = OpenAIOkHttpClientAsync.builder().apiKey(aiModerationConfig.apiKey).build()

    private val resultCache = Caffeine
        .newBuilder()
        .expireAfterWrite(3.hours.toJavaDuration())
        .asLoadingCache<String, ClassificationResult> {
            classifyChatMessage0(it)
        }

    suspend fun classifyChatMessage(message: String): ClassificationResult = resultCache.get(message)

    private suspend fun classifyChatMessage0(message: String): ClassificationResult {
        val params = ModerationCreateParams.builder()
            .input("[GAME_CHAT: MINECRAFT]\n$message")
            .build()

        val result = client.moderations()
            .create(params)
            .await()
            .results()
            .single()

        val flaggedCategoryScores =
            Category.categoriesByCategories(result.categories(), result.categoryScores())

        if (!result.flagged()) {
            return ClassificationResult(
                ClassificationAction.NONE,
                emptyObject2DoubleMap()
            )
        }

        return ClassificationResult(decideAction(flaggedCategoryScores.keys), flaggedCategoryScores)
    }

    data class ClassificationResult(
        val action: ClassificationAction,
        val flaggedScores: Object2DoubleMap<Category>
    )

    private fun decideAction(categories: Set<Category>): ClassificationAction {
        for (action in ClassificationAction.reversedEntries) {
            if (action.categories.any { categories.contains(it) }) return action
        }

        return ClassificationAction.NONE
    }

    enum class ClassificationAction(vararg categories: Category) {
        NONE(
            Category.VIOLENCE
        ),
        SILENT_FLAG(
            Category.ILLICIT,
            Category.ILLICIT_VIOLENT,
            Category.SELF_HARM,
            Category.SELF_HARM_INSTRUCTIONS,
            Category.SELF_HARM_INTENT,
        ),
        DELETE(
            Category.HARASSMENT,
            Category.HARASSMENT_THREATENING,
            Category.HATE,
            Category.SEXUAL,
            Category.VIOLENCE_GRAPHIC
        ),
        MUTE(Category.SEXUAL_MINORS, Category.HATE_THREATENING);

        val categories: EnumSet<Category> = EnumSet.copyOf(categories.toSet())

        companion object {
            val reversedEntries = entries.reversed()
        }
    }

    enum class Category {
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
        VIOLENCE_GRAPHIC;

        companion object {
            fun categoriesByCategories(
                categories: Moderation.Categories,
                scores: Moderation.CategoryScores
            ): Object2DoubleMap<Category> {
                val map = Object2DoubleOpenHashMap<Category>()

                if (categories.harassment()) map.put(HARASSMENT, scores.harassment())
                if (categories.harassmentThreatening()) map.put(
                    HARASSMENT_THREATENING,
                    scores.harassmentThreatening()
                )
                if (categories.hate()) map.put(HATE, scores.hate())
                if (categories.hateThreatening()) map.put(
                    HATE_THREATENING,
                    scores.hateThreatening()
                )
                categories.illicit().ifTrue { map.put(ILLICIT, scores.illicit()) }
                categories.illicitViolent()
                    .ifTrue { map.put(ILLICIT_VIOLENT, scores.illicitViolent()) }
                if (categories.selfHarm()) map.put(SELF_HARM, scores.selfHarm())
                if (categories.selfHarmInstructions()) map.put(
                    SELF_HARM_INSTRUCTIONS,
                    scores.selfHarmInstructions()
                )
                if (categories.selfHarmIntent()) map.put(SELF_HARM_INTENT, scores.selfHarmIntent())
                if (categories.sexual()) map.put(SEXUAL, scores.sexual())
                if (categories.sexualMinors()) map.put(SEXUAL_MINORS, scores.sexualMinors())
                if (categories.violence()) map.put(VIOLENCE, scores.violence())
                if (categories.violenceGraphic()) map.put(
                    VIOLENCE_GRAPHIC,
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
    }
}