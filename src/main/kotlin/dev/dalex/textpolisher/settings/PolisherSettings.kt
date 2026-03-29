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
        var apiEndpoint: String = "",
        var model: String = "claude-haiku-4-5",

        // Enhancement
        var targetLanguage: String = "English",
        var mode: String = "correct-only",
        var customPrompt: String = "",

        // Behavior
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

        val PROVIDERS = listOf("anthropic", "openai", "deepseek", "ollama")
        val MODES = listOf("correct-only", "rephrase", "formal", "concise")
        val LANGUAGES = listOf(
            "English", "Chinese", "French", "German", "Spanish",
            "Japanese", "Korean", "Portuguese", "Russian", "Italian"
        )
    }
}
