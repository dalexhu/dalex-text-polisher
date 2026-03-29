package dev.dalex.textpolisher.api

import com.google.gson.Gson
import com.google.gson.JsonObject
import dev.dalex.textpolisher.prompt.PromptBuilder
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class OllamaClient(
    private val endpoint: String,
    private val model: String,
    timeoutMs: Long = 15_000,
) : AiClient {

    private val gson = Gson()
    private val httpClient = buildHttpClient(timeoutMs)

    override fun complete(prompt: PromptBuilder.Prompt, timeoutMs: Long): String {
        val body = gson.toJson(mapOf(
            "model" to model,
            "system" to prompt.systemMessage,
            "prompt" to prompt.userMessage,
            "stream" to false,
        ))

        val request = Request.Builder()
            .url("${endpoint.trimEnd('/')}/api/generate")
            .header("Content-Type", "application/json")
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()

        val response = httpClient.newCall(request).execute()
        val responseBody = response.body?.string() ?: throw RuntimeException("Empty response from Ollama")

        if (!response.isSuccessful) {
            throw RuntimeException("Ollama error (${response.code}): $responseBody")
        }

        return gson.fromJson(responseBody, JsonObject::class.java)
            .get("response")?.asString
            ?: throw RuntimeException("Unexpected Ollama response format")
    }
}
