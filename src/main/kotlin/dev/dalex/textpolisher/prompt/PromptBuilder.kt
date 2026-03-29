package dev.dalex.textpolisher.prompt

import dev.dalex.textpolisher.settings.PolisherSettings

object PromptBuilder {

    data class Prompt(
        val systemMessage: String,
        val userMessage: String,
    )

    private val MODE_INSTRUCTIONS = mapOf(
        "correct-only" to "Fix only spelling and grammar errors. Do not change the style, tone, or wording in any other way.",
        "rephrase" to "Rephrase the text for natural fluency while preserving the original meaning.",
        "formal" to "Rewrite the text in formal, professional language.",
        "concise" to "Make the text more concise while keeping the meaning intact.",
    )

    fun build(selectedText: String, state: PolisherSettings.State): Prompt {
        val parts = mutableListOf<String>()

        parts.add("You are a text polishing assistant.")
        parts.add(MODE_INSTRUCTIONS[state.mode] ?: MODE_INSTRUCTIONS["correct-only"]!!)
        parts.add("Output in ${state.targetLanguage}.")
        parts.add("Return ONLY the polished text. No explanations, no preamble, no markdown formatting.")
        parts.add("Preserve any comment markers or line prefixes exactly as they appear (e.g. //, /*, *, #, --, <!--, etc.).")

        if (state.customPrompt.isNotBlank()) {
            parts.add(state.customPrompt)
        }

        return Prompt(
            systemMessage = parts.joinToString("\n"),
            userMessage = selectedText,
        )
    }
}
