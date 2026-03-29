package dev.dalex.textpolisher.api

import dev.dalex.textpolisher.prompt.PromptBuilder
import dev.dalex.textpolisher.settings.PolisherSettings

interface AiClient {
    fun complete(prompt: PromptBuilder.Prompt, timeoutMs: Long): String

    companion object {
        fun create(state: PolisherSettings.State, apiKey: String?): AiClient {
            return when (state.provider) {
                "anthropic" -> AnthropicClient(
                    apiKey = apiKey ?: "",
                    endpoint = state.apiEndpoint.ifBlank { "https://api.anthropic.com" },
                    model = state.model,
                )
                "openai" -> OpenAiClient(
                    apiKey = apiKey ?: "",
                    endpoint = state.apiEndpoint.ifBlank { "https://api.openai.com" },
                    model = state.model,
                )
                "deepseek" -> OpenAiClient(
                    apiKey = apiKey ?: "",
                    endpoint = state.apiEndpoint.ifBlank { "https://api.deepseek.com" },
                    model = state.model,
                )
                "ollama" -> OllamaClient(
                    endpoint = state.apiEndpoint.ifBlank { "http://localhost:11434" },
                    model = state.model,
                )
                else -> throw IllegalArgumentException("Unknown provider: ${state.provider}")
            }
        }
    }
}
