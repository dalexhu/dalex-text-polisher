package dev.dalex.textpolisher.api

import dev.dalex.textpolisher.prompt.PromptBuilder
import dev.dalex.textpolisher.settings.PolisherSettings

interface AiClient {
    fun complete(prompt: PromptBuilder.Prompt, timeoutMs: Long): String

    companion object {
        // OpenAI-compatible providers: id → default endpoint
        private val OPENAI_COMPATIBLE = mapOf(
            "openai"   to "https://api.openai.com",
            "deepseek" to "https://api.deepseek.com",
            "groq"     to "https://api.groq.com/openai",
            "mistral"  to "https://api.mistral.ai",
            "gemini"   to "https://generativelanguage.googleapis.com/v1beta/openai",
        )

        fun create(state: PolisherSettings.State, apiKey: String?): AiClient {
            val key = apiKey ?: ""
            val endpoint = state.apiEndpoint.trim()
            val model = state.model

            OPENAI_COMPATIBLE[state.provider]?.let { default ->
                return OpenAiClient(key, endpoint.ifBlank { default }, model)
            }

            return when (state.provider) {
                "anthropic" -> AnthropicClient(key, endpoint.ifBlank { "https://api.anthropic.com" }, model)
                "ollama"    -> OllamaClient(endpoint.ifBlank { "http://localhost:11434" }, model)
                else        -> throw IllegalArgumentException("Unknown provider: ${state.provider}")
            }
        }
    }
}
