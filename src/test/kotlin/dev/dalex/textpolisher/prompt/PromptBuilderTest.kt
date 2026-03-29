package dev.dalex.textpolisher.prompt

import dev.dalex.textpolisher.settings.PolisherSettings
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PromptBuilderTest {

    @Test
    fun `correct-only mode includes grammar instruction`() {
        val state = PolisherSettings.State(mode = "correct-only", targetLanguage = "English")
        val prompt = PromptBuilder.build("Ths is a tset", state)

        assertTrue(prompt.systemMessage.contains("Fix only spelling and grammar"))
        assertTrue(prompt.systemMessage.contains("Do not change the style"))
        assertTrue(prompt.systemMessage.contains("English"))
        assertEquals("Ths is a tset", prompt.userMessage)
    }

    @Test
    fun `rephrase mode includes fluency instruction`() {
        val state = PolisherSettings.State(mode = "rephrase", targetLanguage = "French")
        val prompt = PromptBuilder.build("Hello world", state)

        assertTrue(prompt.systemMessage.contains("Rephrase"))
        assertTrue(prompt.systemMessage.contains("French"))
    }

    @Test
    fun `formal mode includes formal instruction`() {
        val state = PolisherSettings.State(mode = "formal")
        val prompt = PromptBuilder.build("hey what's up", state)

        assertTrue(prompt.systemMessage.contains("formal"))
    }

    @Test
    fun `concise mode includes concise instruction`() {
        val state = PolisherSettings.State(mode = "concise")
        val prompt = PromptBuilder.build("long text here", state)

        assertTrue(prompt.systemMessage.contains("concise"))
    }

    @Test
    fun `custom prompt is appended`() {
        val state = PolisherSettings.State(
            mode = "correct-only",
            customPrompt = "Use British English"
        )
        val prompt = PromptBuilder.build("color", state)

        assertTrue(prompt.systemMessage.contains("Use British English"))
    }

    @Test
    fun `empty custom prompt is not appended`() {
        val state = PolisherSettings.State(mode = "correct-only", customPrompt = "")
        val prompt = PromptBuilder.build("test", state)

        // Should not have trailing blank lines or empty custom prompt
        assertFalse(prompt.systemMessage.endsWith("\n"))
    }

    @Test
    fun `system message always includes no-explanation constraint`() {
        val state = PolisherSettings.State()
        val prompt = PromptBuilder.build("test", state)

        assertTrue(prompt.systemMessage.contains("Return ONLY the polished text"))
    }
}
