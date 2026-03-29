package dev.dalex.textpolisher.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.*
import javax.swing.JPanel
import javax.swing.event.DocumentEvent

class PolisherSettingsComponent {

    val panel: JPanel
    private val providerCombo = com.intellij.openapi.ui.ComboBox(PolisherSettings.PROVIDERS.toTypedArray())
    private val apiKeyField = JBPasswordField()
    private val apiEndpointField = JBTextField()
    private val modelField = JBTextField()
    private val languageCombo = com.intellij.openapi.ui.ComboBox(PolisherSettings.LANGUAGES.toTypedArray())
    private val modeCombo = com.intellij.openapi.ui.ComboBox(PolisherSettings.MODES.toTypedArray())
    private val customPromptField = JBTextField()
    private val resultDisplayCombo = com.intellij.openapi.ui.ComboBox(PolisherSettings.RESULT_DISPLAYS.toTypedArray())
    private val autoApplyBox = JBCheckBox()
    private val requestTimeoutField = JBTextField()
    private val maxSelectionLengthField = JBTextField()

    private var _apiKeyModified = false
    val apiKeyModified: Boolean get() = _apiKeyModified
    fun resetApiKeyModified() { _apiKeyModified = false }

    // Tracks the provider that was active before the combo changes
    private var currentProvider: String = PolisherSettings.getInstance().state.provider

    init {
        apiKeyField.document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) { _apiKeyModified = true }
        })

        // When provider changes: save old values, load new provider's values
        providerCombo.addActionListener {
            val newProvider = providerCombo.selectedItem as? String ?: return@addActionListener
            if (newProvider == currentProvider) return@addActionListener

            // Persist current endpoint/model into the per-provider maps
            val state = PolisherSettings.getInstance().state
            state.providerEndpoints[currentProvider] = apiEndpointField.text
            state.providerModels[currentProvider] = modelField.text

            // Load new provider's saved values, or fall back to defaults
            apiEndpointField.text = state.providerEndpoints[newProvider] ?: ""
            modelField.text = state.providerModels[newProvider]
                ?: PolisherSettings.DEFAULT_MODELS[newProvider] ?: ""

            currentProvider = newProvider

            // Load API key off EDT
            ApplicationManager.getApplication().executeOnPooledThread {
                val storedKey = ApiKeyStorage.get(newProvider) ?: ""
                ApplicationManager.getApplication().invokeLater {
                    apiKeyField.text = storedKey
                    _apiKeyModified = false
                }
            }
        }

        val state = PolisherSettings.getInstance().state
        providerCombo.selectedItem = state.provider
        apiKeyField.text = ApiKeyStorage.get(state.provider) ?: ""
        _apiKeyModified = false
        apiEndpointField.text = state.providerEndpoints[state.provider] ?: ""
        modelField.text = state.providerModels[state.provider]
            ?: PolisherSettings.DEFAULT_MODELS[state.provider] ?: ""
        languageCombo.selectedItem = state.targetLanguage
        modeCombo.selectedItem = state.mode
        customPromptField.text = state.customPrompt
        resultDisplayCombo.selectedItem = state.resultDisplay
        autoApplyBox.isSelected = state.autoApply
        requestTimeoutField.text = state.requestTimeout.toString()
        maxSelectionLengthField.text = state.maxSelectionLength.toString()

        panel = panel {
            group("Connection") {
                row("Provider:") { cell(providerCombo).columns(COLUMNS_MEDIUM) }
                row("API Key:") { cell(apiKeyField).columns(COLUMNS_LARGE) }
                row("Endpoint:") {
                    cell(apiEndpointField).columns(COLUMNS_LARGE)
                        .comment("Leave empty for official endpoint. Set for proxy.")
                }
                row("Model:") { cell(modelField).columns(COLUMNS_MEDIUM) }
            }
            group("Enhancement") {
                row("Target Language:") { cell(languageCombo).columns(COLUMNS_MEDIUM) }
                row("Mode:") { cell(modeCombo).columns(COLUMNS_MEDIUM) }
                row("Custom Prompt:") {
                    cell(customPromptField).columns(COLUMNS_LARGE)
                        .comment("Additional instructions, e.g. \"Use British English\"")
                }
            }
            group("Behavior") {
                row("Result Display:") { cell(resultDisplayCombo).columns(COLUMNS_SHORT) }
                row { cell(autoApplyBox).label("Auto-apply:").comment("Skip preview and replace directly") }
                row("Request Timeout (ms):") { cell(requestTimeoutField).columns(COLUMNS_SHORT) }
                row("Max Selection Length:") { cell(maxSelectionLengthField).columns(COLUMNS_SHORT) }
            }
        }
    }

    var provider: String
        get() = providerCombo.selectedItem as String
        set(value) { providerCombo.selectedItem = value }

    var apiKey: String
        get() = String(apiKeyField.password)
        set(value) { apiKeyField.text = value }

    var apiEndpoint: String
        get() = apiEndpointField.text
        set(value) { apiEndpointField.text = value }

    var model: String
        get() = modelField.text
        set(value) { modelField.text = value }

    var targetLanguage: String
        get() = languageCombo.selectedItem as String
        set(value) { languageCombo.selectedItem = value }

    var mode: String
        get() = modeCombo.selectedItem as String
        set(value) { modeCombo.selectedItem = value }

    var customPrompt: String
        get() = customPromptField.text
        set(value) { customPromptField.text = value }

    var resultDisplay: String
        get() = resultDisplayCombo.selectedItem as String
        set(value) { resultDisplayCombo.selectedItem = value }

    var autoApply: Boolean
        get() = autoApplyBox.isSelected
        set(value) { autoApplyBox.isSelected = value }

    var requestTimeout: Int
        get() = requestTimeoutField.text.toIntOrNull() ?: 15000
        set(value) { requestTimeoutField.text = value.toString() }

    var maxSelectionLength: Int
        get() = maxSelectionLengthField.text.toIntOrNull() ?: 2000
        set(value) { maxSelectionLengthField.text = value.toString() }
}
