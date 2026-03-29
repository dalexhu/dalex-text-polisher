package dev.dalex.textpolisher.api

import dev.dalex.textpolisher.prompt.PromptBuilder
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AnthropicClientTest {

    private lateinit var server: MockWebServer

    @BeforeEach
    fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @AfterEach
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `successful completion returns text content`() {
        server.enqueue(MockResponse()
            .setBody("""
                {
                    "content": [{"type": "text", "text": "This is a test"}],
                    "model": "claude-haiku-4-5",
                    "role": "assistant"
                }
            """.trimIndent())
            .setResponseCode(200)
        )

        val client = AnthropicClient(
            apiKey = "test-key",
            endpoint = server.url("/").toString().trimEnd('/'),
            model = "claude-haiku-4-5"
        )

        val result = client.complete(
            PromptBuilder.Prompt("You are a polisher", "Ths is a tset"),
            timeoutMs = 5000
        )

        assertEquals("This is a test", result)

        val request = server.takeRequest()
        assertEquals("/v1/messages", request.path)
        assertEquals("test-key", request.getHeader("x-api-key"))
        assertEquals("2023-06-01", request.getHeader("anthropic-version"))
        assertTrue(request.body.readUtf8().contains("claude-haiku-4-5"))
    }

    @Test
    fun `API error throws with message`() {
        server.enqueue(MockResponse()
            .setBody("""{"error": {"message": "Invalid API key"}}""")
            .setResponseCode(401)
        )

        val client = AnthropicClient(
            apiKey = "bad-key",
            endpoint = server.url("/").toString().trimEnd('/'),
            model = "claude-haiku-4-5"
        )

        val ex = assertThrows(RuntimeException::class.java) {
            client.complete(
                PromptBuilder.Prompt("system", "user"),
                timeoutMs = 5000
            )
        }

        assertTrue(ex.message!!.contains("Invalid API key"))
        assertTrue(ex.message!!.contains("401"))
    }
}
