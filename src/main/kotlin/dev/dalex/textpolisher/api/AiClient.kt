package dev.dalex.textpolisher.api

import dev.dalex.textpolisher.prompt.PromptBuilder
import dev.dalex.textpolisher.settings.PolisherSettings

/**
 *Interface for an AI client that provides completion functionality.
 */
interface AiClient {
    fun complete(prompt: PromptBuilder.Prompt, timeoutMs: Long): String

    companion object {
        fun create(state: PolisherSettings.State, apiKey: String?): AiClient {
            val key = apiKey ?: ""
            val endpoint = state.apiEndpoint.trim()
            val model = state.model
            return when (state.provider) {
                "anthropic" -> AnthropicClient(
                    apiKey = key,
                    endpoint = endpoint.ifBlank { "https://api.anthropic.com" },
                    model = model,
                )
                "openai" -> OpenAiClient(
                    apiKey = key,
                    endpoint = endpoint.ifBlank { "https://api.openai.com" },
                    model = model,
                )
                "deepseek" -> OpenAiClient(
                    apiKey = key,
                    endpoint = endpoint.ifBlank { "https://api.deepseek.com" },
                    model = model,
                )
                "groq" -> OpenAiClient(
                    apiKey = key,
                    endpoint = endpoint.ifBlank { "https://api.groq.com/openai" },
                    model = model,
                )
                "mistral" -> OpenAiClient(
                    apiKey = key,
                    endpoint = endpoint.ifBlank { "https://api.mistral.ai" },
                    model = model,
                )
                "gemini" -> OpenAiClient(
                    apiKey = key,
                    endpoint = endpoint.ifBlank { "https://generativelanguage.googleapis.com/v1beta/openai" },
                    model = model,
                )
                "ollama" -> OllamaClient(
                    endpoint = endpoint.ifBlank { "http://localhost:11434" },
                    model = model,
                )
                else -> throw IllegalArgumentException("Unknown provider: ${state.provider}")
            }
        }
    }
}
