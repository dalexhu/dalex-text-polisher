package dev.dalex.textpolisher.settings

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class PolisherConfigurable : Configurable {

    private var component: PolisherSettingsComponent? = null

    override fun getDisplayName(): String = "AI Text Polisher"

    override fun createComponent(): JComponent {
        component = PolisherSettingsComponent()
        return component!!.panel
    }

    override fun isModified(): Boolean {
        val c = component ?: return false
        val state = PolisherSettings.getInstance().state
        return c.provider != state.provider
                || c.apiEndpoint != (state.providerEndpoints[c.provider] ?: "")
                || c.model != (state.providerModels[c.provider] ?: PolisherSettings.DEFAULT_MODELS[c.provider] ?: "")
                || c.targetLanguage != state.targetLanguage
                || c.mode != state.mode
                || c.customPrompt != state.customPrompt
                || c.resultDisplay != state.resultDisplay
                || c.autoApply != state.autoApply
                || c.requestTimeout != state.requestTimeout
                || c.maxSelectionLength != state.maxSelectionLength
                || c.apiKeyModified
    }

    override fun apply() {
        val c = component ?: return
        val settings = PolisherSettings.getInstance()
        settings.state.provider = c.provider
        settings.state.providerEndpoints[c.provider] = c.apiEndpoint
        settings.state.providerModels[c.provider] = c.model
        settings.state.targetLanguage = c.targetLanguage
        settings.state.mode = c.mode
        settings.state.customPrompt = c.customPrompt
        settings.state.resultDisplay = c.resultDisplay
        settings.state.autoApply = c.autoApply
        settings.state.requestTimeout = c.requestTimeout
        settings.state.maxSelectionLength = c.maxSelectionLength

        if (c.apiKeyModified) {
            ApiKeyStorage.set(c.provider, c.apiKey)
            c.resetApiKeyModified()
        }
    }

    override fun reset() {
        val c = component ?: return
        val state = PolisherSettings.getInstance().state
        c.provider = state.provider
        c.apiEndpoint = state.providerEndpoints[state.provider] ?: ""
        c.model = state.providerModels[state.provider] ?: PolisherSettings.DEFAULT_MODELS[state.provider] ?: ""
        c.targetLanguage = state.targetLanguage
        c.mode = state.mode
        c.customPrompt = state.customPrompt
        c.resultDisplay = state.resultDisplay
        c.autoApply = state.autoApply
        c.requestTimeout = state.requestTimeout
        c.maxSelectionLength = state.maxSelectionLength
        c.resetApiKeyModified()
    }

    override fun disposeUIResources() {
        component = null
    }
}
