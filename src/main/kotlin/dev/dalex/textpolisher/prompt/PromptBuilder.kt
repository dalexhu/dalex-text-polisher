package dev.dalex.textpolisher.prompt

import dev.dalex.textpolisher.settings.PolisherSettings
import java.util.Locale

object PromptBuilder {

    data class Prompt(
        val systemMessage: String,
        val userMessage: String,
    )

    private fun resolveLanguage(setting: String): String {
        if (setting != PolisherSettings.FOLLOW_SYSTEM) return setting
        return PolisherSettings.LOCALE_TO_LANGUAGE[Locale.getDefault().language] ?: "English"
    }

    private val GLOBAL_RULES = """
        You are a text polishing assistant.

        Always follow these rules:
        - Preserve the original meaning unless explicitly allowed
        - Do not add explanations or comments
        - Do not include quotation marks
        - Output only the final result
        - Keep the language natural and fluent
        - Preserve any comment markers or line prefixes exactly as they appear (e.g. //, /*, *, #, --, <!--)
        - Capitalize proper nouns, product names, and technical acronyms correctly (e.g. MySQL, HTTP, JSON, API, GitHub)
    """.trimIndent()

    private val MODE_INSTRUCTIONS = mapOf(
        "correct-only" to """
            Correct all language errors, including spelling, grammar, punctuation, word choice, and missing articles.
            Preserve the original sentence structure as much as possible.
            Only make minimal changes necessary to ensure correctness and naturalness.
            Do not change the meaning.
            Do not add extra content.
        """.trimIndent(),
        "rephrase" to """
            Rewrite the text to be natural and fluent.
            You may change sentence structure and wording freely,
            but must preserve the original meaning.
            Do not add new information.
        """.trimIndent(),
        "formal" to """
            Rewrite the text in a formal and professional tone.
            Replace informal expressions, slang, and abbreviations with appropriate formal language.
            Ensure grammatical correctness and completeness.
            Do not change the core meaning.
        """.trimIndent(),
        "concise" to """
            Rewrite the text to be as concise as possible.
            Remove redundancy and unnecessary words.
            You may restructure the sentence to reduce length,
            while preserving the original meaning.
        """.trimIndent(),
    )

    fun build(selectedText: String, state: PolisherSettings.State): Prompt {
        val parts = mutableListOf<String>()

        parts.add(GLOBAL_RULES)
        parts.add("Mode: ${state.mode}\n\n${MODE_INSTRUCTIONS[state.mode] ?: MODE_INSTRUCTIONS["correct-only"]!!}")
        parts.add("Write the entire output in ${resolveLanguage(state.targetLanguage)}. Do not mix languages.")

        if (state.customPrompt.isNotBlank()) {
            parts.add(state.customPrompt)
        }

        return Prompt(
            systemMessage = parts.joinToString("\n"),
            userMessage = selectedText,
        )
    }
}
