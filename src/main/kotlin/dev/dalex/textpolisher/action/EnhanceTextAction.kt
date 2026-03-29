package dev.dalex.textpolisher.action

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import dev.dalex.textpolisher.api.AiClient
import dev.dalex.textpolisher.prompt.PromptBuilder
import dev.dalex.textpolisher.settings.ApiKeyStorage
import dev.dalex.textpolisher.settings.PolisherSettings
import dev.dalex.textpolisher.ui.DiffResultHandler

class EnhanceTextAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabledAndVisible = editor?.selectionModel?.hasSelection() == true
    }

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val project = e.project ?: return
        val selectionModel = editor.selectionModel
        val selectedText = selectionModel.selectedText ?: return

        val settings = PolisherSettings.getInstance()

        // Validate selection length
        if (selectedText.length > settings.state.maxSelectionLength) {
            notify(
                project,
                "Selection too long (${selectedText.length} chars). Maximum is ${settings.state.maxSelectionLength}.",
                NotificationType.WARNING
            )
            return
        }

        // Check API key
        val apiKey = ApiKeyStorage.get()
        if (apiKey.isNullOrBlank() && settings.state.provider != "ollama") {
            notify(
                project,
                "API key not configured. Please set it in Settings > Tools > AI Text Polisher.",
                NotificationType.ERROR
            )
            return
        }

        // Run AI call in background
        object : Task.Backgroundable(project, "AI Text Polisher: Enhancing...", true) {
            override fun run(indicator: ProgressIndicator) {
                indicator.isIndeterminate = true
                try {
                    val prompt = PromptBuilder.build(selectedText, settings.state)
                    val client = AiClient.create(settings.state, apiKey)
                    val result = client.complete(prompt, settings.state.requestTimeout.toLong())

                    // Show result on EDT
                    DiffResultHandler.show(project, editor, selectedText, result, settings.state.autoApply)
                } catch (ex: Exception) {
                    notify(project, "Enhancement failed: ${ex.message}", NotificationType.ERROR)
                }
            }
        }.queue()
    }

    private fun notify(project: com.intellij.openapi.project.Project, content: String, type: NotificationType) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("AI Text Polisher")
            .createNotification(content, type)
            .notify(project)
    }
}
