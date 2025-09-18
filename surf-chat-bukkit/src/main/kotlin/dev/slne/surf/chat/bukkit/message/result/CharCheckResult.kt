package dev.slne.surf.chat.bukkit.message.result

data class CharCheckResult(
    val validInput: Boolean,
    val invalidChars: String? = null,
) {
    companion object {
        private val validCharactersRegex =
            "^[\\u0020-\\u007EäöüÄÖÜß€@£¥|²³µ½¼¾«»¡¿°§´`^~¨]+$".toRegex()

        fun of(input: String): CharCheckResult {
            val invalidChars = input.filter { !validCharactersRegex.matches(it.toString()) }
            return CharCheckResult(invalidChars.isEmpty(), invalidChars.ifEmpty { null })
        }
    }
}