package dev.dalex.textpolisher.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@Service
@State(name = "AITextPolisherSettings", storages = [Storage("AITextPolisher.xml")])
class PolisherSettings : PersistentStateComponent<PolisherSettings.State> {

    private var myState = State()

    data class State(
        // Connection
        var provider: String = "anthropic",

        // Per-provider endpoint/model overrides (empty string = use default endpoint)
        var providerEndpoints: MutableMap<String, String> = mutableMapOf(),
        var providerModels: MutableMap<String, String> = mutableMapOf(),

        // Enhancement
        var targetLanguage: String = "English",
        var mode: String = "correct-only",
        var customPrompt: String = "",

        // Behavior
        var resultDisplay: String = "inline",
        var autoApply: Boolean = false,
        var requestTimeout: Int = 15000,
        var maxSelectionLength: Int = 2000,
    )

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    companion object {
        fun getInstance(): PolisherSettings =
            ApplicationManager.getApplication().getService(PolisherSettings::class.java)

        val PROVIDERS = listOf("anthropic", "openai", "deepseek", "groq", "mistral", "gemini", "ollama")

        val DEFAULT_MODELS = mapOf(
            "anthropic" to "claude-haiku-4-5-20251001",
            "openai"    to "gpt-4o-mini",
            "deepseek"  to "deepseek-chat",
            "groq"      to "llama-3.3-70b-versatile",
            "mistral"   to "mistral-large-latest",
            "gemini"    to "gemini-2.0-flash",
            "ollama"    to "llama3.2",
        )
        val MODES = listOf("correct-only", "rephrase", "formal", "concise")
        val RESULT_DISPLAYS = listOf("inline", "diff")
        val LANGUAGES = listOf(
            "English", "Chinese", "French", "German", "Spanish",
            "Japanese", "Korean", "Portuguese", "Russian", "Italian"
        )
    }
}
